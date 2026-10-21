/* Customer "My orders" page with live status tracking. */
(function () {
  'use strict';
  const F = window.Foodie;
  const STEPS = ['PLACED', 'ACCEPTED', 'PREPARING', 'READY_FOR_PICKUP', 'COLLECTED'];
  const SHORT = { PLACED: 'Placed', ACCEPTED: 'Accepted', PREPARING: 'Preparing', READY_FOR_PICKUP: 'Ready', COLLECTED: 'Picked up' };
  let highlight = new URLSearchParams(location.search).get('placed');

  function stepper(o) {
    if (o.status === 'CANCELLED') return `<div class="muted" style="margin:10px 0">This order was cancelled${o.cancelReason ? ': ' + F.esc(o.cancelReason) : '.'}</div>`;
    const idx = STEPS.indexOf(o.status);
    return `<div class="steps" aria-label="Order progress">${STEPS.map((s, i) =>
      `<div class="step ${i <= idx ? 'done' : ''} ${i === idx ? 'current' : ''}">${SHORT[s]}</div>`).join('')}</div>`;
  }

  function card(o) {
    const ready = o.status === 'READY_FOR_PICKUP' ? `<div class="notice" style="margin:8px 0">Your order is ready. Show order <b>#${F.esc(o.orderNumber)}</b> at the counter to collect it.</div>` : '';
    return `<article class="order" id="order-${o.id}" ${String(o.id) === highlight ? 'style="border-color:var(--gold)"' : ''}>
      <div class="row spread wrap"><h3>Order #${F.esc(o.orderNumber)} &middot; ${F.esc(o.restaurantName)}</h3>${F.badge(o.status)}</div>
      <div class="muted">${F.dateTime(o.createdAt)}</div>
      ${stepper(o)}
      ${F.orderBody(o)}
      ${ready}
      <div class="actions">${o.status === 'PLACED' ? `<button class="btn danger small" data-cancel="${o.id}" type="button">Cancel order</button>` : ''}</div>
    </article>`;
  }

  async function load() {
    const box = document.getElementById('orders');
    try {
      const orders = await F.api('/orders/mine');
      box.innerHTML = orders.length ? orders.map(card).join('') : '<div class="empty">You have not placed any orders yet.<br><a class="btn mt" href="/">Browse the menu</a></div>';
      if (highlight) { const el = document.getElementById('order-' + highlight); if (el) el.scrollIntoView({ block: 'center' }); highlight = null; }
    } catch (e) {
      if (!box.querySelector('.order')) box.innerHTML = `<div class="empty">${F.esc(e.message)}</div>`;
    }
  }

  document.addEventListener('DOMContentLoaded', () => {
    if (!F.requireRole('CUSTOMER')) return;
    document.getElementById('orders').addEventListener('click', async (e) => {
      const b = e.target.closest('[data-cancel]'); if (!b) return;
      if (!F.confirmAction('Cancel this order?')) return;
      b.disabled = true;
      try { await F.api(`/orders/${b.dataset.cancel}/cancel`, { method: 'POST', body: {} }); F.toast('Order cancelled.', 'success'); }
      catch (err) { F.toast(err.message, 'error'); }
      load();
    });
    F.poll(load, 12000);
  });
})();
