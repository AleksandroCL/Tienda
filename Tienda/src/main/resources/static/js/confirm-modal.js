document.addEventListener('DOMContentLoaded', () => {
    const overlay = document.getElementById('modalEliminar');
    const btnCancelar = document.getElementById('modalCancelar');
    const btnConfirmar = document.getElementById('modalConfirmar');
    if (!overlay || !btnCancelar || !btnConfirmar) return;

    let formPendiente = null;

    document.querySelectorAll('.delete-form').forEach(form => {
        form.addEventListener('submit', (e) => {
            e.preventDefault();
            formPendiente = form;
            overlay.classList.add('activo');
        });
    });

    btnCancelar.addEventListener('click', () => {
        formPendiente = null;
        overlay.classList.remove('activo');
    });

    btnConfirmar.addEventListener('click', () => {
        if (formPendiente) {
            formPendiente.submit();
        }
    });

    overlay.addEventListener('click', (e) => {
        if (e.target === overlay) {
            overlay.classList.remove('activo');
            formPendiente = null;
        }
    });
});