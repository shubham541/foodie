/* Login and registration pages. */
(function () {
  'use strict';
  const F = window.Foodie;
  const params = new URLSearchParams(location.search);

  /** Only same-site relative paths are allowed as post-login destinations (prevents open redirects). */
  const safeNext = (n) => (n && /^\/(?!\/)[\w\-\/]*$/.test(n) ? n : null);

  function done(auth) {
    F.session.save(auth);
    location.href = safeNext(params.get('next')) || F.HOME_BY_ROLE[auth.user.role] || '/';
  }

  function wire(form, path, build) {
    const btn = F.$('#submit', form);
    form.addEventListener('submit', async (e) => {
      e.preventDefault();
      F.clearFormError(form);
      btn.disabled = true;
      try {
        done(await F.api(path, { method: 'POST', body: build(form), auth: false }));
      } catch (err) {
        F.showFormError(form, err);
        btn.disabled = false;
      }
    });
  }

  document.addEventListener('DOMContentLoaded', () => {
    if (F.session.token && F.session.user) { location.replace(F.HOME_BY_ROLE[F.session.user.role] || '/'); return; }

    const login = document.getElementById('login-form');
    if (login) {
      if (params.get('expired')) document.getElementById('expired').classList.remove('hidden');
      wire(login, '/auth/login', (f) => ({ email: f.email.value.trim(), password: f.password.value }));
    }

    const reg = document.getElementById('register-form');
    if (reg) {
      wire(reg, '/auth/register', (f) => ({ name: f.name.value.trim(), email: f.email.value.trim(), phone: f.phone.value.trim(), password: f.password.value }));
    }
  });
})();
