/* Storefront: menu browsing, cart drawer and checkout. */
(function () {
  'use strict';
  const F = window.Foodie;
  const state = { restaurant: null, menu: [], byId: new Map(), category: null, tag: null, query: '', vegOnly: false };
  const TAGS = [{ key: 'bestseller', label: '⭐ Bestsellers' }, { key: 'spicy', label: '🌶 Spicy' }, { key: 'healthy', label: '🥗 Healthy picks' }];

  const $ = (id) => document.getElementById(id);

  /* ---------------- loading ---------------- */
  async function load() {
    try {
      const list = await F.api('/restaurants', { auth: false });
      if (!list.length) { $('menu').innerHTML = '<div class="empty">No restaurant is available right now.</div>'; return; }
      state.restaurant = list[0];
      renderHero();
      const menu = await F.api(`/restaurants/${state.restaurant.id}/menu`, { auth: false });
      state.menu = menu.categories;
      state.byId = new Map();
      state.menu.forEach((c) => c.items.forEach((i) => state.byId.set(i.id, i)));
      if (F.cart.sync(state.byId)) F.toast('Some items in your cart changed or are unavailable.');
      renderChips();
      renderMenu();
    } catch (e) {
      $('menu').innerHTML = `<div class="empty">${F.esc(e.message)} <br><button class="btn small mt" id="retry" type="button">Retry</button></div>`;
      const r = $('retry'); if (r) r.addEventListener('click', load);
    }
  }

  function renderHero() {
    const r = state.restaurant;
    $('r-name').textContent = r.name;
    $('r-desc').textContent = r.description || '';
    const pill = $('r-status');
    pill.classList.toggle('closed', !r.acceptingOrders);
    pill.lastElementChild.textContent = r.acceptingOrders ? 'Accepting orders' : 'Currently closed';
    $('r-cuisine').textContent = r.cuisine || '';
    $('r-cuisine').classList.toggle('hidden', !r.cuisine);
    $('r-tax').textContent = `GST ${r.taxRatePercent}% added at checkout`;
    $('closed-notice').classList.toggle('hidden', r.acceptingOrders);
  }

  /* ---------------- menu ---------------- */
  function renderChips() {
    const chips = [{ name: null, label: 'All' }].concat(state.menu.map((c) => ({ name: c.name, label: c.name })));
    $('chips').innerHTML = chips.map((c) =>
      `<button type="button" class="chip ${!state.tag && state.category === c.name ? 'active' : ''}" data-cat="${F.esc(c.name ?? '')}">${F.esc(c.label)}</button>`).join('')
      + TAGS.map((t) => `<button type="button" class="chip tag ${state.tag === t.key ? 'active' : ''}" data-tag="${t.key}">${t.label}</button>`).join('');
  }

  function matches(item) {
    if (state.vegOnly && item.veg !== true) return false;
    if (state.tag && !item[state.tag]) return false;
    if (state.query) {
      const q = state.query.toLowerCase();
      if (!(item.name.toLowerCase().includes(q) || (item.description || '').toLowerCase().includes(q) || item.category.toLowerCase().includes(q))) return false;
    }
    return true;
  }

  function dishCard(i) {
    const q = F.cart.qty(i.id);
    const canOrder = state.restaurant.acceptingOrders && i.available;
    const pic = i.imageUrl
      ? `<div class="pic" role="img" aria-label="${F.esc(i.name)}" style="background-image:url('${F.esc(i.imageUrl)}')"></div>`
      : '<div class="pic placeholder"><img src="/images/brand/logo.jpg" alt=""></div>';
    const dot = i.veg === true ? '<span class="vdot" title="Vegetarian"></span>' : i.veg === false ? '<span class="vdot nonveg" title="Contains egg/non-veg"></span>' : '<span class="vdot unknown"></span>';
    let action;
    if (!i.available) action = '<span class="muted">Unavailable</span>';
    else if (!canOrder) action = '';
    else if (q > 0) action = `<span class="stepper" data-id="${i.id}"><button type="button" data-act="dec" aria-label="Remove one">&minus;</button><span>${q}</span><button type="button" data-act="inc" aria-label="Add one">+</button></span>`;
    else action = `<button type="button" class="btn small" data-act="add" data-id="${i.id}">Add</button>`;
    const tags = [i.bestseller ? '<span class="tag best">⭐ Bestseller</span>' : '', i.spicy ? '<span class="tag spicy">🌶 Spicy</span>' : '',
      i.healthy ? '<span class="tag healthy">🥗 Healthy</span>' : ''].join('');
    return `<article class="dish ${i.available ? '' : 'unavailable'}">${pic}<div class="body">
      <div class="name">${dot}<span>${F.esc(i.name)}</span></div>
      ${tags ? `<div class="tags">${tags}</div>` : ''}
      ${i.description ? `<div class="desc">${F.esc(i.description)}</div>` : ''}
      <div class="foot"><span class="price">${F.money(i.price)}</span>${action}</div></div></article>`;
  }

  function renderMenu() {
    const sections = state.menu
      .filter((c) => !state.category || c.name === state.category)
      .map((c) => ({ name: c.name, items: c.items.filter(matches) }))
      .filter((c) => c.items.length);
    const star = !state.category && !state.tag && !state.query && !state.vegOnly
      ? state.menu.flatMap((c) => c.items).filter((i) => i.bestseller && i.available) : [];
    const strip = star.length
      ? `<section class="menu-section" id="cat-bestsellers"><h2>⭐ Bestsellers</h2><div class="grid cols-3">${star.map(dishCard).join('')}</div></section>` : '';
    $('menu').innerHTML = sections.length
      ? strip + sections.map((c) => `<section class="menu-section" id="cat-${F.esc(c.name.replace(/\W+/g, '-'))}"><h2>${F.esc(c.name)}</h2><div class="grid cols-3">${c.items.map(dishCard).join('')}</div></section>`).join('')
      : '<div class="empty">No dishes match your search.</div>';
  }

  document.addEventListener('DOMContentLoaded', () => {
    $('chips').addEventListener('click', (e) => {
      const b = e.target.closest('[data-cat],[data-tag]'); if (!b) return;
      if (b.dataset.tag) { state.tag = state.tag === b.dataset.tag ? null : b.dataset.tag; state.category = null; }
      else { state.category = b.dataset.cat || null; state.tag = null; }
      renderChips(); renderMenu();
    });
    $('search').addEventListener('input', (e) => { state.query = e.target.value.trim(); renderMenu(); });
    $('veg-only').addEventListener('change', (e) => { state.vegOnly = e.target.checked; renderMenu(); });

    $('menu').addEventListener('click', (e) => {
      const b = e.target.closest('[data-act]'); if (!b) return;
      const holder = b.closest('[data-id]');
      const id = Number(holder.dataset.id);
      const item = state.byId.get(id);
      if (b.dataset.act === 'add' || b.dataset.act === 'inc') F.cart.add(state.restaurant.id, item);
      else F.cart.setQty(id, F.cart.qty(id) - 1);
    });

    document.addEventListener('cart:changed', () => { if (state.restaurant) { renderMenu(); if (!$('drawer').classList.contains('hidden')) renderDrawer(); } });
    document.addEventListener('click', (e) => { if (e.target.closest('#cart-btn')) openDrawer(); });
    $('overlay').addEventListener('click', closeDrawer);
    $('drawer-close').addEventListener('click', closeDrawer);
    document.addEventListener('keydown', (e) => { if (e.key === 'Escape') closeDrawer(); });
    load();
  });

  /* ---------------- cart drawer & checkout ---------------- */
  let checkoutMode = false;

  function openDrawer() { checkoutMode = false; $('overlay').classList.remove('hidden'); $('drawer').classList.remove('hidden'); renderDrawer(); }
  function closeDrawer() { $('overlay').classList.add('hidden'); $('drawer').classList.add('hidden'); }

  function totals() {
    const r = state.restaurant;
    const subtotal = F.round2(F.cart.lines().reduce((s, l) => s + l.price * l.qty, 0));
    const tax = F.round2(subtotal * r.taxRatePercent / 100);
    return { subtotal, tax, total: F.round2(subtotal + tax) };
  }

  function totalsHtml(t) {
    return `<div class="totals"><div><span>Item total</span><span>${F.money(t.subtotal)}</span></div>
      <div><span>GST (${state.restaurant.taxRatePercent}%)</span><span>${F.money(t.tax)}</span></div>
      <div class="grand"><span>To pay</span><span>${F.money(t.total)}</span></div></div>`;
  }

  function renderDrawer() {
    const body = $('drawer-body'), foot = $('drawer-foot');
    const lines = F.cart.lines();
    if (!lines.length) {
      body.innerHTML = '<div class="empty">Your cart is empty.<br>Add something delicious!</div>'; foot.innerHTML = ''; return;
    }
    if (checkoutMode) { renderCheckout(body, foot); return; }
    body.innerHTML = lines.map((l) => `<div class="cart-line" data-id="${l.id}">
      <div class="info"><b>${F.esc(l.name)}</b><span class="muted">${F.money(l.price)} each</span></div>
      <span class="stepper"><button type="button" data-d="-1" aria-label="Remove one">&minus;</button><span>${l.qty}</span><button type="button" data-d="1" aria-label="Add one">+</button></span>
      <b style="min-width:64px;text-align:right">${F.money(l.price * l.qty)}</b></div>`).join('');
    body.querySelectorAll('[data-d]').forEach((b) => b.addEventListener('click', () => {
      const id = Number(b.closest('[data-id]').dataset.id); F.cart.setQty(id, F.cart.qty(id) + Number(b.dataset.d));
    }));
    const t = totals();
    foot.innerHTML = totalsHtml(t) + `<button class="btn block mt" id="to-checkout" type="button" ${state.restaurant.acceptingOrders ? '' : 'disabled'}>Proceed to checkout</button>
      <button class="btn ghost block mt" id="clear-cart" type="button" style="margin-top:8px">Clear cart</button>`;
    $('to-checkout').addEventListener('click', startCheckout);
    $('clear-cart').addEventListener('click', () => F.cart.clear());
  }

  function startCheckout() {
    const user = F.session.user;
    if (!user || !F.session.token) { location.href = '/login?next=' + encodeURIComponent('/'); return; }
    if (user.role !== 'CUSTOMER') { F.toast('Please log in with a customer account to place orders.', 'error'); return; }
    checkoutMode = true; renderDrawer();
  }

  function renderCheckout(body, foot) {
    const user = F.session.user;
    body.innerHTML = `<form id="co-form" novalidate>
      <div class="notice">Pickup only: collect your order from House of Shukita when it is marked ready.</div>
      <label for="contactPhone">Contact number</label><input id="contactPhone" name="contactPhone" type="tel" inputmode="numeric" value="${F.esc(user.phone || '')}" maxlength="10">
      <label>Payment (at pickup)</label>
      <div class="radio-row"><label><input type="radio" name="paymentMethod" value="CASH" checked> Cash</label><label><input type="radio" name="paymentMethod" value="UPI"> UPI</label></div>
      <label for="notes">Instructions (optional)</label><textarea id="notes" name="notes" rows="2" maxlength="300" placeholder="Less spicy, extra cheese..."></textarea>
    </form>`;
    foot.innerHTML = totalsHtml(totals()) + `<button class="btn block mt" id="place" type="button">Place order</button>
      <button class="btn ghost block" id="back" type="button" style="margin-top:8px">Back to cart</button>`;
    const form = $('co-form');
    $('back').addEventListener('click', () => { checkoutMode = false; renderDrawer(); });
    $('place').addEventListener('click', () => placeOrder(form));
  }

  async function placeOrder(form) {
    F.clearFormError(form);
    const btn = $('place');
    const f = form.elements;
    const body = {
      restaurantId: state.restaurant.id,
      items: F.cart.lines().map((l) => ({ menuItemId: l.id, quantity: l.qty })),
      contactPhone: f.contactPhone.value.trim(),
      paymentMethod: f.paymentMethod.value,
      notes: f.notes.value.trim() || null
    };
    btn.disabled = true;
    try {
      const order = await F.api('/orders', { method: 'POST', body });
      F.cart.clear();
      F.toast(`Order #${order.orderNumber} placed!`, 'success');
      location.href = '/orders?placed=' + order.id;
    } catch (err) {
      F.showFormError(form, err);
      if (err.status === 409) { load(); }
      btn.disabled = false;
    }
  }
})();
