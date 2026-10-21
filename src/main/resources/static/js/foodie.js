/* Foodie core: session, API client, cart, helpers and shared header behaviour. */
(function () {
  'use strict';
  const TOKEN_KEY = 'foodie.token';
  const USER_KEY = 'foodie.user';
  const CART_KEY = 'foodie.cart';
  const F = (window.Foodie = {});

  const safe = {
    get(k) { try { return localStorage.getItem(k); } catch (e) { return null; } },
    set(k, v) { try { localStorage.setItem(k, v); } catch (e) { /* storage unavailable */ } },
    del(k) { try { localStorage.removeItem(k); } catch (e) { /* ignore */ } }
  };

  /* ---------- helpers ---------- */
  F.esc = (s) => String(s ?? '').replace(/[&<>"']/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
  F.money = (n) => {
    const v = Number(n || 0);
    return '₹' + v.toLocaleString('en-IN', { minimumFractionDigits: Number.isInteger(v) ? 0 : 2, maximumFractionDigits: 2 });
  };
  F.dateTime = (iso) => (iso ? new Date(iso).toLocaleString('en-IN', { day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit' }) : '');
  F.$ = (sel, root) => (root || document).querySelector(sel);
  F.$$ = (sel, root) => Array.from((root || document).querySelectorAll(sel));
  F.round2 = (n) => Math.round((n + Number.EPSILON) * 100) / 100;

  F.STATUS_LABELS = {
    PLACED: 'Order placed', ACCEPTED: 'Accepted', PREPARING: 'Preparing', READY_FOR_PICKUP: 'Ready for pickup',
    COLLECTED: 'Picked up', CANCELLED: 'Cancelled'
  };
  F.badge = (status) => `<span class="badge ${F.esc(status)}">${F.esc(F.STATUS_LABELS[status] || status)}</span>`;
  F.HOME_BY_ROLE = { CUSTOMER: '/orders', RESTAURANT: '/dashboard/restaurant' };

  /** Item list + price breakdown shared by every order view. */
  F.orderBody = (o, opts) => {
    const compact = opts && opts.compact;
    const items = `<ul>${o.items.map((i) => `<li><span>${i.quantity} &times; ${F.esc(i.name)}</span><span>${F.money(i.lineTotal)}</span></li>`).join('')}</ul>`;
    const totals = `<div class="totals"><div><span>Item total</span><span>${F.money(o.subtotal)}</span></div>
      <div><span>GST</span><span>${F.money(o.taxAmount)}</span></div>
      <div class="grand"><span>Total (${o.paymentMethod === 'CASH' ? 'cash' : 'UPI'} at pickup)</span><span>${F.money(o.total)}</span></div></div>`;
    return items + totals + (compact || !o.notes ? '' : `<div class="muted" style="margin-top:8px">Your note: ${F.esc(o.notes)}</div>`);
  };

  F.toast = (message, type) => {
    let box = document.getElementById('toasts');
    if (!box) { box = document.createElement('div'); box.id = 'toasts'; box.setAttribute('role', 'status'); document.body.appendChild(box); }
    const t = document.createElement('div');
    t.className = 'toast ' + (type || '');
    t.textContent = message;
    box.appendChild(t);
    setTimeout(() => t.remove(), type === 'error' ? 6000 : 3500);
  };

  /* ---------- session ---------- */
  F.session = {
    get token() { return safe.get(TOKEN_KEY); },
    get user() { try { return JSON.parse(safe.get(USER_KEY) || 'null'); } catch (e) { return null; } },
    save(auth) { safe.set(TOKEN_KEY, auth.token); safe.set(USER_KEY, JSON.stringify(auth.user)); },
    clear() { safe.del(TOKEN_KEY); safe.del(USER_KEY); }
  };
  F.logout = () => { F.session.clear(); location.href = '/'; };

  /** Redirects to login unless the signed-in user has one of the roles. Returns the user or null. */
  F.requireRole = (...roles) => {
    const u = F.session.user;
    if (!F.session.token || !u) { location.href = '/login?next=' + encodeURIComponent(location.pathname); return null; }
    if (roles.length && !roles.includes(u.role)) { location.href = F.HOME_BY_ROLE[u.role] || '/'; return null; }
    return u;
  };

  /* ---------- API client ---------- */
  class ApiError extends Error {
    constructor(message, status, fieldErrors) { super(message); this.status = status; this.fieldErrors = fieldErrors || {}; }
  }
  F.ApiError = ApiError;

  F.api = async (path, opts) => {
    const o = opts || {};
    const headers = { Accept: 'application/json' };
    if (o.body !== undefined) headers['Content-Type'] = 'application/json';
    const token = F.session.token;
    if (token && o.auth !== false) headers.Authorization = 'Bearer ' + token;
    let res;
    try {
      res = await fetch('/api/v1' + path, { method: o.method || 'GET', headers, body: o.body !== undefined ? JSON.stringify(o.body) : undefined });
    } catch (e) {
      throw new ApiError('Cannot reach the server. Check your connection and try again.', 0);
    }
    let data = null;
    const text = await res.text();
    if (text) { try { data = JSON.parse(text); } catch (e) { data = null; } }
    if (!res.ok) {
      if (res.status === 401 && token && o.auth !== false) {
        F.session.clear();
        if (!/^\/(login|register)/.test(location.pathname)) location.href = '/login?next=' + encodeURIComponent(location.pathname) + '&expired=1';
      }
      throw new ApiError((data && data.message) || 'Something went wrong (' + res.status + ').', res.status, data && data.fieldErrors);
    }
    return data;
  };

  /** Shows API errors inside a form: general message in `.form-error`, field messages under inputs. */
  F.showFormError = (form, err) => {
    F.$$('.field-error', form).forEach((n) => n.remove());
    let box = F.$('.form-error', form);
    if (!box) { box = document.createElement('div'); box.className = 'form-error'; box.setAttribute('role', 'alert'); form.prepend(box); }
    const fields = err.fieldErrors || {};
    const names = Object.keys(fields);
    names.forEach((name) => {
      const input = form.elements[name];
      if (input && input.parentNode) {
        const m = document.createElement('div'); m.className = 'field-error'; m.textContent = fields[name];
        input.parentNode.insertBefore(m, input.nextSibling);
      }
    });
    box.textContent = names.length ? 'Please fix the highlighted fields.' : err.message;
  };
  F.clearFormError = (form) => { F.$$('.field-error,.form-error', form).forEach((n) => n.remove()); };

  /* ---------- cart (browser only; prices are re-computed by the server on checkout) ---------- */
  const readCart = () => { try { return JSON.parse(safe.get(CART_KEY) || 'null') || { restaurantId: null, items: {} }; } catch (e) { return { restaurantId: null, items: {} }; } };
  const writeCart = (c) => { safe.set(CART_KEY, JSON.stringify(c)); document.dispatchEvent(new CustomEvent('cart:changed')); };
  F.cart = {
    get() { return readCart(); },
    lines() { return Object.values(readCart().items); },
    count() { return this.lines().reduce((n, l) => n + l.qty, 0); },
    qty(id) { const l = readCart().items[id]; return l ? l.qty : 0; },
    add(restaurantId, item) {
      const c = readCart();
      c.restaurantId = restaurantId;
      const l = c.items[item.id] || { id: item.id, name: item.name, price: item.price, veg: item.veg, qty: 0 };
      l.qty = Math.min(l.qty + 1, 20); l.price = item.price; l.name = item.name;
      c.items[item.id] = l; writeCart(c);
    },
    setQty(id, qty) {
      const c = readCart();
      if (!c.items[id]) return;
      if (qty <= 0) delete c.items[id]; else c.items[id].qty = Math.min(qty, 20);
      writeCart(c);
    },
    clear() { writeCart({ restaurantId: null, items: {} }); },
    sync(menuItems) { // drop lines that no longer exist or became unavailable, refresh prices
      const c = readCart(); let changed = false;
      Object.keys(c.items).forEach((id) => {
        const m = menuItems.get(Number(id));
        if (!m || !m.available) { delete c.items[id]; changed = true; }
        else if (m.price !== c.items[id].price) { c.items[id].price = m.price; changed = true; }
      });
      if (changed) writeCart(c);
      return changed;
    }
  };

  /* ---------- header ---------- */
  F.initHeader = () => {
    const user = F.session.user;
    const nav = document.getElementById('nav');
    if (!nav) return;
    const path = location.pathname;
    const link = (href, label) => `<a href="${href}" class="${path === href ? 'active' : ''}">${label}</a>`;
    let html = link('/', 'Menu');
    if (user) {
      html += link(F.HOME_BY_ROLE[user.role] || '/', user.role === 'CUSTOMER' ? 'My orders' : 'Dashboard');
      const shown = user.name.length > 18 ? user.name.slice(0, 17) + '…' : user.name;
      html += `<span class="muted" style="padding:0 6px" title="${F.esc(user.name)}">${F.esc(shown)}</span><button class="link" id="logout-btn" type="button">Log out</button>`;
    } else {
      html += link('/login', 'Log in') + '<a class="btn small" href="/register">Sign up</a>';
    }
    if (!user || user.role === 'CUSTOMER') {
      html += '<button class="link cart-btn" id="cart-btn" type="button" aria-label="Open cart">Cart<span class="cart-count hidden" id="cart-count">0</span></button>';
    }
    nav.innerHTML = html;
    const out = document.getElementById('logout-btn');
    if (out) out.addEventListener('click', F.logout);
    const refresh = () => {
      const n = F.cart.count(); const b = document.getElementById('cart-count');
      if (b) { b.textContent = n; b.classList.toggle('hidden', n === 0); }
    };
    document.addEventListener('cart:changed', refresh); refresh();
  };

  document.addEventListener('DOMContentLoaded', F.initHeader);

  /* ---------- polling helper that pauses while the tab is hidden ---------- */
  F.poll = (fn, ms) => {
    fn();
    const id = setInterval(() => { if (!document.hidden) fn(); }, ms);
    document.addEventListener('visibilitychange', () => { if (!document.hidden) fn(); });
    return id;
  };

  F.confirmAction = (msg) => window.confirm(msg);
})();
