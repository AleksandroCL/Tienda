(() => {
    document.querySelectorAll('[data-password-toggle]').forEach(button => {
        const input = document.getElementById(button.getAttribute('aria-controls'));
        if (!input) return;
        const fieldName = input.id === 'confirmarPassword' ? 'confirmación de contraseña' : 'contraseña';
        button.hidden = false;
        button.closest('.password-control').classList.add('has-toggle');
        button.addEventListener('click', () => {
            const visible = input.type === 'password';
            input.type = visible ? 'text' : 'password';
            button.textContent = visible ? 'Ocultar' : 'Mostrar';
            button.setAttribute('aria-pressed', String(visible));
            button.setAttribute('aria-label', `${visible ? 'Ocultar' : 'Mostrar'} ${fieldName}`);
        });
    });
    document.querySelector('[data-form-feedback]')?.focus();
})();
