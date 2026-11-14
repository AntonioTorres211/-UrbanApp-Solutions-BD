/* UrbanApp Solutions – Home
   Comportamentos: menu móvel, exemplo no mapa, login de demonstração (só no navegador). */
(function () {
  'use strict';
  var $ = function (s, r) { return (r || document).querySelector(s); };
  var $$ = function (s, r) { return Array.prototype.slice.call((r || document).querySelectorAll(s)); };

  /* ---------- Rodapé: ano ---------- */
  var y = $('#year'); if (y) y.textContent = '© ' + new Date().getFullYear();

  /* ---------- Menu móvel ---------- */
  var menuBtn = $('#menuBtn'), nav = $('#nav');
  menuBtn.addEventListener('click', function () {
    var open = nav.classList.toggle('open');
    menuBtn.setAttribute('aria-expanded', open ? 'true' : 'false');
  });
  $$('#nav a').forEach(function (a) {
    a.addEventListener('click', function () {
      nav.classList.remove('open'); menuBtn.setAttribute('aria-expanded', 'false');
    });
  });

  /* ---------- Exemplo ilustrativo: solicitações no mapa ---------- */
  var tickets = [
    { cat: 'Buraco na via', bairro: 'Tijuca', prio: 5, etapa: 1, extra: 'Prioridade 5: destacada como crítica.' },
    { cat: 'Coleta seletiva não passou', bairro: 'Penha', prio: 3, etapa: 0, extra: 'Aguardando uma equipe assumir o pedido.' },
    { cat: 'Poda de árvore', bairro: 'Botafogo', prio: 2, etapa: 2, extra: 'Solução registrada pela equipe de parques. Reaberta 1 vez.' }
  ];
  var pins = $$('.pin');
  function show(i) {
    var t = tickets[i];
    $('#tCat').textContent = t.cat;
    $('#tBairro').textContent = t.bairro;
    $('#tPrio').textContent = t.prio;
    var b = $('#tBadge');
    b.textContent = t.prio === 5 ? 'Crítica' : 'Prioridade ' + t.prio;
    b.classList.toggle('critica', t.prio === 5);
    $$('#tSteps li').forEach(function (li, k) {
      li.classList.toggle('on', k <= t.etapa);
      li.classList.toggle('now', k === t.etapa);
    });
    $('#tExtra').textContent = t.extra;
    pins.forEach(function (p, k) { p.setAttribute('aria-pressed', k === i ? 'true' : 'false'); });
  }
  pins.forEach(function (p) {
    p.addEventListener('click', function () { show(Number(p.dataset.i)); });
  });
  show(0);

  /* ---------- Login de demonstração ---------- */
  var dlg = $('#loginDlg'), form = $('#loginForm'), err = $('#loginErr');
  var loginBtn = $('#loginBtn'), logoutBtn = $('#logoutBtn'), logoutNav = $('#logoutNav'), hello = $('#hello');
  var email = $('#email'), senha = $('#senha');
  var KEY = 'urbanapp_usuario';
  var NEW_REQUEST_URL = 'solicitacao.html'; // próxima página do site (formulário de solicitação)

  function readUser() { try { return sessionStorage.getItem(KEY); } catch (e) { return null; } }
  function saveUser(v) { try { sessionStorage.setItem(KEY, v); } catch (e) {} }
  function clearUser() { try { sessionStorage.removeItem(KEY); } catch (e) {} }

  function toast(msg) {
    var t = $('#toast'); t.textContent = msg; t.hidden = false;
    clearTimeout(toast.id); toast.id = setTimeout(function () { t.hidden = true; }, 3600);
  }
  function setLoggedIn(name) {
    hello.textContent = 'Olá, ' + name; hello.hidden = false; logoutBtn.hidden = false; logoutNav.hidden = false;
    loginBtn.innerHTML = '<span class="long">Nova solicitação</span><span class="short">Solicitar</span>';
    loginBtn.dataset.logged = '1';
    $$('[data-open-login]').forEach(function (b) { b.textContent = 'Fazer uma solicitação'; b.dataset.logged = '1'; });
  }
  function setLoggedOut() {
    hello.hidden = true; logoutBtn.hidden = true; logoutNav.hidden = true;
    loginBtn.innerHTML = '<span class="long">Entrar para solicitar</span><span class="short">Entrar</span>';
    delete loginBtn.dataset.logged;
    $$('[data-open-login]').forEach(function (b) { b.textContent = 'Entrar e fazer uma solicitação'; delete b.dataset.logged; });
  }
  function openOrGo(btn) {
    if (btn.dataset.logged) { window.location.href = NEW_REQUEST_URL; return; }
    err.hidden = true; email.classList.remove('bad'); senha.classList.remove('bad');
    if (typeof dlg.showModal === 'function') dlg.showModal(); else dlg.setAttribute('open', '');
    email.focus();
  }
  [loginBtn].concat($$('[data-open-login]')).forEach(function (b) {
    b.addEventListener('click', function () { openOrGo(b); });
  });
  $('#closeDlg').addEventListener('click', function () { dlg.close(); });
  dlg.addEventListener('click', function (e) { if (e.target === dlg) dlg.close(); });

  form.addEventListener('submit', function (e) {
    e.preventDefault();
    var em = email.value.trim(), pw = senha.value;
    email.classList.remove('bad'); senha.classList.remove('bad');
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(em)) {
      err.textContent = 'Digite um e-mail válido, como voce@exemplo.com.';
      err.hidden = false; email.classList.add('bad'); email.focus(); return;
    }
    if (pw.length < 6) {
      err.textContent = 'A senha precisa ter pelo menos 6 caracteres.';
      err.hidden = false; senha.classList.add('bad'); senha.focus(); return;
    }
    var name = em.split('@')[0].replace(/[._-]+/g, ' ');
    name = name.charAt(0).toUpperCase() + name.slice(1);
    saveUser(name); setLoggedIn(name); dlg.close(); form.reset();
    toast('Você entrou. Agora é só abrir sua solicitação.');
  });
  [logoutBtn, logoutNav].forEach(function (b) {
    b.addEventListener('click', function () { clearUser(); setLoggedOut(); nav.classList.remove('open'); toast('Você saiu da sua conta.'); });
  });

  var saved = readUser(); if (saved) setLoggedIn(saved);
})();
