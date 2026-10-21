/* Restaurant owner dashboard (the owner runs everything): live board, all orders, menu, customers, reports. */
(function () {
  'use strict';
  const F = window.Foodie;
  const NEXT = {
    PLACED: { to: 'ACCEPTED', label: 'Accept' },
    ACCEPTED: { to: 'PREPARING', label: 'Start preparing' },
    PREPARING: { to: 'READY_FOR_PICKUP', label: 'Mark ready' },
    READY_FOR_PICKUP: { to: 'COLLECTED', label: 'Handed over' }
  };
  const COLUMNS = [
    { title: 'New', match: ['PLACED'] },
    { title: 'In the kitchen', match: ['ACCEPTED', 'PREPARING'] },
    { title: 'Ready for pickup', match: ['READY_FOR_PICKUP'] },
    { title: 'Done today', match: ['COLLECTED', 'CANCELLED'] }
  ];
  let lastPlaced = null;

  function orderCard(o) {
    const n = NEXT[o.status];
    const canReject = ['PLACED', 'ACCEPTED', 'PREPARING', 'READY_FOR_PICKUP'].includes(o.status);
    return `<article class="order">
      <div class="row spread"><h3>#${F.esc(o.orderNumber)}</h3>${F.badge(o.status)}</div>
      <div class="muted">${F.esc(o.customerName)} &middot; ${F.esc(o.contactPhone)} &middot; ${F.dateTime(o.createdAt)}</div>
      ${F.orderBody(o, { compact: true })}
      ${o.notes ? `<div class="notice" style="margin:8px 0">Note: ${F.esc(o.notes)}</div>` : ''}
      <div class="actions">
        ${n ? `<button class="btn small" data-id="${o.id}" data-to="${n.to}" type="button">${n.label}</button>` : ''}
        ${canReject ? `<button class="btn danger small" data-id="${o.id}" data-to="CANCELLED" type="button">${o.status === 'PLACED' ? 'Reject' : 'Cancel'}</button>` : ''}
      </div></article>`;
  }

  async function loadOrders() {
    try {
      const orders = await F.api('/restaurant-admin/orders');
      const newest = orders.filter((o) => o.status === 'PLACED').map((o) => o.id);
      if (lastPlaced && newest.some((id) => !lastPlaced.includes(id))) F.toast('New order received!', 'success');
      lastPlaced = newest;
      document.getElementById('board').innerHTML = COLUMNS.map((c) => {
        const list = orders.filter((o) => c.match.includes(o.status));
        return `<div class="col"><h3>${c.title}<span class="muted">${list.length}</span></h3>${list.map(orderCard).join('') || '<div class="muted">Nothing here.</div>'}</div>`;
      }).join('');
    } catch (e) { document.getElementById('board').innerHTML = `<div class="empty">${F.esc(e.message)}</div>`; }
  }

  const $ = (id) => document.getElementById(id);

  async function loadStats() {
    try {
      const s = await F.api('/restaurant-admin/summary');
      const tiles = [['Total orders', s.totalOrders], ['Pending', s.pendingOrders], ['Ready for pickup', s.readyForPickup], ['Picked up', s.completedOrders],
        ['Cancelled', s.cancelledOrders], ['Active customers', s.activeUsers], ['Revenue (picked up)', F.money(s.revenue)]];
      $('stats').innerHTML = tiles.map(([k, v]) => `<div class="stat"><b>${F.esc(v)}</b><span class="muted">${k}</span></div>`).join('');
    } catch (e) { $('stats').innerHTML = `<div class="empty">${F.esc(e.message)}</div>`; }
  }

  async function loadHistory() {
    const rows = $('history-rows');
    try {
      const orders = await F.api('/restaurant-admin/orders?filter=ALL');
      rows.innerHTML = orders.map((o) => `<tr><td>${F.esc(o.orderNumber)}</td><td>${F.esc(o.customerName)}<br><span class="muted">${F.esc(o.contactPhone)}</span></td>
        <td>${o.items.map((i) => `${i.quantity}&times; ${F.esc(i.name)}`).join(', ')}</td><td>${F.money(o.total)}</td><td>${F.badge(o.status)}</td><td>${F.dateTime(o.createdAt)}</td>
        <td>${['COLLECTED', 'CANCELLED'].includes(o.status) ? '' : `<button class="btn danger small" data-cancel="${o.id}" type="button">Cancel</button>`}</td></tr>`).join('')
        || '<tr><td colspan="7" class="muted">No orders yet.</td></tr>';
    } catch (e) { rows.innerHTML = `<tr><td colspan="7">${F.esc(e.message)}</td></tr>`; }
  }

  async function loadCustomers() {
    const rows = $('customer-rows');
    try {
      const users = await F.api('/restaurant-admin/customers');
      rows.innerHTML = users.map((u) => `<tr><td>${F.esc(u.name)}</td><td>${F.esc(u.email)}</td><td>${F.esc(u.phone || '')}</td>
        <td>${u.active ? '<span class="badge COLLECTED">Active</span>' : '<span class="badge CANCELLED">Deactivated</span>'}</td>
        <td><button class="btn ghost small" data-toggle="${u.id}" data-active="${u.active}" type="button">${u.active ? 'Deactivate' : 'Activate'}</button></td></tr>`).join('')
        || '<tr><td colspan="5" class="muted">No customers yet.</td></tr>';
    } catch (e) { rows.innerHTML = `<tr><td colspan="5">${F.esc(e.message)}</td></tr>`; }
  }

  async function loadReports() {
    try {
      const r = await F.api('/restaurant-admin/reports');
      const top = (r.topItems || []).map((i) => `<li><span>${F.esc(i.name)}</span><b>${i.quantity} sold</b></li>`).join('') || '<li class="muted">No data yet.</li>';
      $('reports').innerHTML = `<div class="card"><h3>Average time to pickup</h3><p>${r.averagePickupMinutes != null ? Math.round(r.averagePickupMinutes) + ' min' : 'No data yet'}</p></div>
        <div class="card"><h3>Average order value</h3><p>${F.money(r.averageOrderValue)}</p></div>
        <div class="card"><h3>Top dishes</h3><ul class="order" style="border:0;padding:0;margin:0">${top}</ul></div>`;
    } catch (e) { $('reports').innerHTML = `<div class="empty">${F.esc(e.message)}</div>`; }
  }

  async function loadMenu() {
    const rows = document.getElementById('menu-rows');
    try {
      const items = await F.api('/restaurant-admin/menu');
      rows.innerHTML = items.map((i) => `<tr data-id="${i.id}">
        <td>${F.esc(i.name)}</td><td class="muted">${F.esc(i.category)}</td>
        <td><input type="number" min="1" step="1" value="${i.price}" aria-label="Price of ${F.esc(i.name)}"></td>
        <td><label class="switch"><input type="checkbox" ${i.available ? 'checked' : ''} aria-label="Available"> <span>${i.available ? 'Yes' : 'Sold out'}</span></label></td>
        <td><button class="btn ghost small" data-save type="button">Save price</button></td></tr>`).join('');
    } catch (e) { rows.innerHTML = `<tr><td colspan="5">${F.esc(e.message)}</td></tr>`; }
  }

  document.addEventListener('DOMContentLoaded', async () => {
    if (!F.requireRole('RESTAURANT')) return;
    const accepting = document.getElementById('accepting');

    try {
      const r = await F.api('/restaurant-admin/restaurant');
      document.getElementById('r-title').textContent = r.name;
      document.getElementById('r-sub').textContent = 'Live order board - refreshes automatically';
      accepting.checked = r.acceptingOrders;
    } catch (e) { F.toast(e.message, 'error'); }

    accepting.addEventListener('change', async () => {
      try { await F.api('/restaurant-admin/settings', { method: 'PATCH', body: { acceptingOrders: accepting.checked } }); F.toast(accepting.checked ? 'Now accepting orders.' : 'Orders paused.', 'success'); }
      catch (e) { accepting.checked = !accepting.checked; F.toast(e.message, 'error'); }
    });

    document.querySelectorAll('[data-tab]').forEach((b) => b.addEventListener('click', () => {
      document.querySelectorAll('[data-tab]').forEach((x) => x.classList.toggle('active', x === b));
      ['orders', 'history', 'menu', 'customers', 'reports'].forEach((t) => $('tab-' + t).classList.toggle('hidden', t !== b.dataset.tab));
      if (b.dataset.tab === 'menu') loadMenu();
      if (b.dataset.tab === 'history') loadHistory();
      if (b.dataset.tab === 'customers') loadCustomers();
      if (b.dataset.tab === 'reports') loadReports();
    }));

    $('history-rows').addEventListener('click', async (e) => {
      const b = e.target.closest('[data-cancel]'); if (!b || !F.confirmAction('Cancel this order?')) return;
      try { await F.api(`/orders/${b.dataset.cancel}/cancel`, { method: 'POST', body: { reason: 'Cancelled by restaurant' } }); F.toast('Order cancelled.', 'success'); }
      catch (err) { F.toast(err.message, 'error'); }
      loadHistory(); loadStats(); loadOrders();
    });
    $('customer-rows').addEventListener('click', async (e) => {
      const b = e.target.closest('[data-toggle]'); if (!b) return;
      try { await F.api(`/restaurant-admin/customers/${b.dataset.toggle}/active`, { method: 'PATCH', body: { active: b.dataset.active !== 'true' } }); }
      catch (err) { F.toast(err.message, 'error'); }
      loadCustomers(); loadStats();
    });

    document.getElementById('board').addEventListener('click', async (e) => {
      const b = e.target.closest('[data-to]'); if (!b) return;
      if (b.dataset.to === 'CANCELLED' && !F.confirmAction('Reject this order?')) return;
      b.disabled = true;
      try { await F.api(`/orders/${b.dataset.id}/status`, { method: 'PATCH', body: { status: b.dataset.to, reason: b.dataset.to === 'CANCELLED' ? 'Rejected by restaurant' : undefined } }); }
      catch (err) { F.toast(err.message, 'error'); }
      loadOrders(); loadStats();
    });

    const rows = document.getElementById('menu-rows');
    async function patch(tr, body, okMsg) {
      try { await F.api(`/restaurant-admin/menu/${tr.dataset.id}`, { method: 'PATCH', body }); F.toast(okMsg, 'success'); }
      catch (err) { F.toast(err.message, 'error'); loadMenu(); }
    }
    rows.addEventListener('change', (e) => {
      if (e.target.type !== 'checkbox') return;
      const tr = e.target.closest('tr');
      tr.querySelector('.switch span').textContent = e.target.checked ? 'Yes' : 'Sold out';
      patch(tr, { available: e.target.checked }, e.target.checked ? 'Marked available.' : 'Marked sold out.');
    });
    rows.addEventListener('click', (e) => {
      if (!e.target.closest('[data-save]')) return;
      const tr = e.target.closest('tr');
      patch(tr, { price: Number(tr.querySelector('input[type=number]').value) }, 'Price updated.');
    });

    loadStats();
    F.poll(loadOrders, 10000);
  });
})();
