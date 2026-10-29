document.addEventListener("DOMContentLoaded", () => {
  
  // --- 1. FONDO GEOMÉTRICO ANIMADO (Diseño Oscuro) ---
  const canvas = document.getElementById("bg-canvas");
  
  if (canvas) {
      // Fondo del canvas: negro
      canvas.style.background = "#070707";
      const ctx = canvas.getContext("2d");
      let width, height, particles;

      function init() {
        width = canvas.width = window.innerWidth;
        height = canvas.height = window.innerHeight;
        particles = [];

        for (let i = 0; i < 60; i++) {
          particles.push({
            x: Math.random() * width,
            y: Math.random() * height,
            vx: (Math.random() - 0.5) * 1.0,
            vy: (Math.random() - 0.5) * 1.0,
            radius: Math.random() * 3 + 1,
          });
        }
      }

      function animate() {
        requestAnimationFrame(animate);
        ctx.clearRect(0, 0, width, height);

        // Nodos (puntos blancos)
        ctx.fillStyle = "#ffffff";
        // Líneas de conexión (blanco translúcido)
        ctx.strokeStyle = "rgba(255, 255, 255, 0.25)";
        ctx.lineWidth = 1;

        particles.forEach((p, index) => {
          p.x += p.vx;
          p.y += p.vy;

          if (p.x < 0 || p.x > width) p.vx *= -1;
          if (p.y < 0 || p.y > height) p.vy *= -1;

          ctx.beginPath();
          ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2);
          ctx.fill();

          for (let j = index + 1; j < particles.length; j++) {
            const p2 = particles[j];
            const dist = Math.hypot(p.x - p2.x, p.y - p2.y);

            if (dist < 180) {
              ctx.beginPath();
              ctx.moveTo(p.x, p.y);
              ctx.lineTo(p2.x, p2.y);
              ctx.stroke();
            }
          }
        });
      }

      window.addEventListener("resize", init);
      init();
      animate();
  }

  // --- 2. VALIDACIÓN DEL FORMULARIO ---
  const loginForm = document.getElementById("loginForm");
  
  if (loginForm) {
    loginForm.addEventListener("submit", (e) => {
      // Verificamos que los inputs existan antes de leer su valor
      const userNode = document.getElementById("username");
      const passNode = document.getElementById("password");

      if (userNode && passNode) {
          const username = userNode.value;
          const password = passNode.value;

          if (username.trim() === "" || password.trim() === "") {
            e.preventDefault(); // Detiene el envío

            // Añade clase para animación de error
            loginForm.classList.add("form-error-shake");

            // Remueve la clase después de la animación para poder repetirla
            setTimeout(() => {
              loginForm.classList.remove("form-error-shake");
            }, 500);
          }
      }
    });
  }
});