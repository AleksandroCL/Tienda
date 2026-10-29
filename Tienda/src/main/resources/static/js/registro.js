(() => {
    const form = document.getElementById('registroForm');
    if (!form) return;
    const password = document.getElementById('password');
    const confirmation = document.getElementById('confirmarPassword');
    const validate = () => {
        const value = password.value;
        const valid = [...value].length >= 8 && new TextEncoder().encode(value).length <= 72
            && /\p{Lu}/u.test(value) && /\p{Nd}/u.test(value)
            && /[^\p{L}\p{N}\s\p{Cc}]/u.test(value);
        password.setCustomValidity(value && !valid
            ? 'Usa al menos 8 caracteres, una mayúscula, un número y un símbolo. Máximo 72 bytes UTF-8.' : '');
        confirmation.setCustomValidity(confirmation.value && confirmation.value !== value
            ? 'Las contraseñas no coinciden.' : '');
    };
    password.addEventListener('input', validate);
    confirmation.addEventListener('input', validate);
    form.addEventListener('submit', event => {
        validate();
        if (!form.reportValidity()) event.preventDefault();
    });
})();
