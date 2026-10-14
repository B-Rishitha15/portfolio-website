document.addEventListener('DOMContentLoaded', function () {

  // ---------- Scroll progress + back-to-top ----------
  var progress = document.getElementById('progress');
  var toTop = document.getElementById('toTop');
  window.addEventListener('scroll', function () {
    var max = document.documentElement.scrollHeight - window.innerHeight;
    var pct = max > 0 ? (window.scrollY / max) * 100 : 0;
    progress.style.width = pct + '%';
    if (toTop) {
      toTop.classList.toggle('opacity-0', window.scrollY < 600);
      toTop.classList.toggle('pointer-events-none', window.scrollY < 600);
    }
  }, { passive: true });
  if (toTop) toTop.addEventListener('click', function () { window.scrollTo({ top: 0, behavior: 'smooth' }); });

  // ---------- Mouse glow ----------
  var glow = document.getElementById('cursorGlow');
  if (glow && window.matchMedia('(pointer: fine)').matches) {
    window.addEventListener('mousemove', function (e) {
      glow.style.left = e.clientX + 'px';
      glow.style.top = e.clientY + 'px';
    });
  }

  // ---------- Typing effect ----------
  var typed = document.getElementById('typed');
  if (typed) {
    var roles = ['Java Developer (Fresher)', 'Spring Boot Learner', 'AI & ML Enthusiast', 'Problem Solver'];
    var r = 0, c = 0, deleting = false;
    (function tick() {
      var current = roles[r];
      typed.textContent = current.substring(0, c);
      if (!deleting && c === current.length) { deleting = true; return setTimeout(tick, 1500); }
      if (deleting && c === 0) { deleting = false; r = (r + 1) % roles.length; }
      c += deleting ? -1 : 1;
      setTimeout(tick, deleting ? 35 : 80);
    })();
  }

  // ---------- Particle network (hero) ----------
  var canvas = document.getElementById('particles');
  if (canvas) {
    var ctx = canvas.getContext('2d');
    var w = 0, h = 0, pts = [], mouse = { x: null, y: null };
    var resize = function () {
      w = canvas.width = canvas.offsetWidth;
      h = canvas.height = canvas.offsetHeight;
      var n = Math.min(90, Math.floor((w * h) / 16000));
      pts = [];
      for (var i = 0; i < n; i++) {
        pts.push({ x: Math.random() * w, y: Math.random() * h,
                   vx: (Math.random() - 0.5) * 0.5, vy: (Math.random() - 0.5) * 0.5 });
      }
    };
    resize();
    window.addEventListener('resize', resize);
    canvas.parentElement.addEventListener('mousemove', function (e) {
      var rect = canvas.getBoundingClientRect();
      mouse.x = e.clientX - rect.left;
      mouse.y = e.clientY - rect.top;
    });
    canvas.parentElement.addEventListener('mouseleave', function () { mouse.x = null; mouse.y = null; });

    (function draw() {
      ctx.clearRect(0, 0, w, h);
      for (var i = 0; i < pts.length; i++) {
        var p = pts[i];
        p.x += p.vx; p.y += p.vy;
        if (p.x < 0 || p.x > w) p.vx *= -1;
        if (p.y < 0 || p.y > h) p.vy *= -1;
        ctx.beginPath();
        ctx.arc(p.x, p.y, 1.8, 0, Math.PI * 2);
        ctx.fillStyle = 'rgba(165,180,252,.8)';
        ctx.fill();
        for (var j = i + 1; j < pts.length; j++) {
          var q = pts[j], dx = p.x - q.x, dy = p.y - q.y, d = Math.sqrt(dx * dx + dy * dy);
          if (d < 120) {
            ctx.strokeStyle = 'rgba(129,140,248,' + (0.18 * (1 - d / 120)) + ')';
            ctx.beginPath(); ctx.moveTo(p.x, p.y); ctx.lineTo(q.x, q.y); ctx.stroke();
          }
        }
        if (mouse.x !== null) {
          var mx = p.x - mouse.x, my = p.y - mouse.y, md = Math.sqrt(mx * mx + my * my);
          if (md < 160) {
            ctx.strokeStyle = 'rgba(34,211,238,' + (0.45 * (1 - md / 160)) + ')';
            ctx.beginPath(); ctx.moveTo(p.x, p.y); ctx.lineTo(mouse.x, mouse.y); ctx.stroke();
          }
        }
      }
      requestAnimationFrame(draw);
    })();
  }

  // ---------- Scroll reveal ----------
  var revealObserver = new IntersectionObserver(function (entries) {
    entries.forEach(function (entry) {
      if (entry.isIntersecting) {
        var delay = entry.target.getAttribute('data-delay') || 0;
        setTimeout(function () { entry.target.classList.add('visible'); }, delay);
        revealObserver.unobserve(entry.target);
      }
    });
  }, { threshold: 0.12 });
  document.querySelectorAll('.reveal').forEach(function (el, idx) {
    if (!el.getAttribute('data-delay')) el.setAttribute('data-delay', (idx % 4) * 90);
    revealObserver.observe(el);
  });

  // ---------- Animated counters ----------
  var counterObserver = new IntersectionObserver(function (entries) {
    entries.forEach(function (entry) {
      if (!entry.isIntersecting) return;
      var el = entry.target, target = parseInt(el.getAttribute('data-count'), 10), cur = 0;
      var step = Math.max(1, Math.ceil(target / 30));
      var timer = setInterval(function () {
        cur = Math.min(target, cur + step);
        el.textContent = cur;
        if (cur >= target) clearInterval(timer);
      }, 45);
      counterObserver.unobserve(el);
    });
  }, { threshold: 0.6 });
  document.querySelectorAll('[data-count]').forEach(function (el) { counterObserver.observe(el); });

  // ---------- Active nav link ----------
  var links = document.querySelectorAll('.nav-link');
  var sectionObserver = new IntersectionObserver(function (entries) {
    entries.forEach(function (entry) {
      if (entry.isIntersecting) {
        links.forEach(function (l) {
          l.classList.toggle('active', l.getAttribute('href') === '#' + entry.target.id);
        });
      }
    });
  }, { rootMargin: '-45% 0px -50% 0px' });
  document.querySelectorAll('main section[id]').forEach(function (s) { sectionObserver.observe(s); });

  // ---------- Mobile menu ----------
  var menuBtn = document.getElementById('menuBtn');
  var mobileMenu = document.getElementById('mobileMenu');
  if (menuBtn && mobileMenu) {
    menuBtn.addEventListener('click', function () { mobileMenu.classList.toggle('hidden'); });
    mobileMenu.querySelectorAll('a').forEach(function (a) {
      a.addEventListener('click', function () { mobileMenu.classList.add('hidden'); });
    });
  }

  // ---------- Skills tabs ----------
  var tabs = document.querySelectorAll('.skill-tab');
  var items = document.querySelectorAll('.skill-item');
  tabs.forEach(function (tab) {
    tab.addEventListener('click', function () {
      tabs.forEach(function (t) { t.classList.remove('active'); });
      tab.classList.add('active');
      var filter = tab.getAttribute('data-filter');
      items.forEach(function (item) {
        var show = filter === 'all' || item.getAttribute('data-cat') === filter;
        item.style.display = show ? '' : 'none';
      });
    });
  });

  // ---------- 3D tilt cards ----------
  if (window.matchMedia('(pointer: fine)').matches) {
    document.querySelectorAll('.tilt').forEach(function (card) {
      card.addEventListener('mousemove', function (e) {
        var rect = card.getBoundingClientRect();
        var x = (e.clientX - rect.left) / rect.width - 0.5;
        var y = (e.clientY - rect.top) / rect.height - 0.5;
        card.style.transform = 'perspective(900px) rotateY(' + (x * 10) + 'deg) rotateX(' + (-y * 10) + 'deg) translateY(-6px)';
      });
      card.addEventListener('mouseleave', function () { card.style.transform = ''; });
    });
  }

  // ---------- Contact form ----------
  var message = document.getElementById('message');
  var counter = document.getElementById('msgCount');
  if (message && counter) {
    var update = function () { counter.textContent = message.value.length + ' / 3000'; };
    message.addEventListener('input', update);
    update();
  }
  var form = document.getElementById('contactForm');
  if (form) {
    form.addEventListener('submit', function () {
      var btn = document.getElementById('sendBtn');
      btn.disabled = true;
      btn.textContent = 'Sending...';
    });
  }
});