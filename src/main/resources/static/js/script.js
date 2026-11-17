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

  /* ---------- Cadastro e login ---------- */
  var dlg = $('#loginDlg'), form = $('#loginForm');
  var loginBtn = $('#loginBtn'), logoutBtn = $('#logoutBtn'), logoutNav = $('#logoutNav'), hello = $('#hello');
  var email = $('#email'), senha = $('#senha'), nome = $('#nome');
  var authToggle = $('#authToggle'), isRegistering = false;
  var NEW_REQUEST_URL = '/solicitacoes/nova';

  function toast(msg, type) {
    var t = $('#toast');
    t.textContent = msg;
    t.classList.remove('success', 'error');
    if (type) t.classList.add(type);
    t.hidden = false;
    clearTimeout(toast.id); toast.id = setTimeout(function () { t.hidden = true; }, 3600);
  }

  function displayName(address) {
    var name = address.split('@')[0].replace(/[._-]+/g, ' ');
    return name.charAt(0).toUpperCase() + name.slice(1);
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

  function setAuthMode(registering) {
    isRegistering = registering;
    $('#loginTitle').textContent = registering ? 'Criar uma conta' : 'Entrar para fazer uma solicitação';
    $('#authSubmit').textContent = registering ? 'Cadastrar' : 'Entrar';
    authToggle.textContent = registering ? 'Já tem uma conta? Entrar' : 'Ainda não tem uma conta? Cadastre-se';
    $('#nameField').hidden = !registering;
    nome.required = registering;
    $('#passwordRules').hidden = !registering;
    $('#authHint').textContent = registering
      ? 'Sua senha será armazenada de forma protegida.'
      : 'Use seu e-mail e sua senha cadastrados.';
    senha.autocomplete = registering ? 'new-password' : 'current-password';
    email.classList.remove('bad');
    senha.classList.remove('bad');
    nome.classList.remove('bad');
  }

  function openOrGo(btn) {
    if (btn.dataset.logged) { window.location.href = NEW_REQUEST_URL; return; }
    setAuthMode(false);
    if (typeof dlg.showModal === 'function') dlg.showModal(); else dlg.setAttribute('open', '');
    email.focus();
  }

  [loginBtn].concat($$('[data-open-login]')).forEach(function (b) {
    b.addEventListener('click', function () { openOrGo(b); });
  });

  $('#closeDlg').addEventListener('click', function () { dlg.close(); });
  dlg.addEventListener('cancel', function (e) { e.preventDefault(); });
  authToggle.addEventListener('click', function () { setAuthMode(!isRegistering); });

  form.addEventListener('submit', async function (e) {
    e.preventDefault();
    email.classList.remove('bad'); senha.classList.remove('bad');
    nome.classList.remove('bad');
    try {
      var payload = { email: email.value, password: senha.value };
      if (isRegistering) payload.name = nome.value;
      var response = await fetch(isRegistering ? '/api/auth/register' : '/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      var result = await response.json();

      if (!response.ok) {
        email.classList.add('bad');
        senha.classList.add('bad');
        if (isRegistering) nome.classList.add('bad');
        toast(result.message || 'E-mail ou senha inválidos.', 'error');
        return;
      }

      if (isRegistering) {
        senha.value = '';
        setAuthMode(false);
        dlg.close();
        toast('Usuário cadastrado com sucesso. Agora, faça login.', 'success');
        return;
      }

      setLoggedIn(result.name || displayName(result.email));
      senha.value = '';
      dlg.close();
      window.location.assign(result.role === 'ADMIN' ? '/adm' : NEW_REQUEST_URL);
    } catch (error) {
      toast('Não foi possível concluir a operação. Verifique a conexão e tente novamente.', 'error');
    }
  });

  [logoutBtn, logoutNav].forEach(function (button) {
    button.addEventListener('click', async function () {
      try {
        var response = await fetch('/api/auth/logout', { method: 'POST' });
        if (!response.ok) {
          throw new Error('Falha ao encerrar a sessão.');
        }
        setLoggedOut();
        nav.classList.remove('open');
        toast('Você saiu da sua conta.', 'success');
      } catch (error) {
        toast('Não foi possível encerrar a sessão. Tente novamente.', 'error');
      }
    });
  });

  fetch('/api/auth/session')
    .then(function (response) {
      if (!response.ok) throw new Error('Falha ao consultar sessão.');
      return response.json();
    })
    .then(function (result) {
      if (result.authenticated) setLoggedIn(result.name || displayName(result.email));
      else setLoggedOut();
    })
    .catch(function () {
      toast('Não foi possível verificar a sessão. Atualize a página.', 'error');
    });
})();
