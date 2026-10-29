(() => {
    const dialogs = [...document.querySelectorAll('.support-dialog')];
    if (typeof HTMLDialogElement !== 'undefined' && typeof HTMLDialogElement.prototype.showModal === 'function') {
        dialogs.forEach(dialog => {
            let opener = null;
            let startedOutside = false;
            const outside = event => {
                const rect = dialog.getBoundingClientRect();
                return event.clientX < rect.left || event.clientX > rect.right
                    || event.clientY < rect.top || event.clientY > rect.bottom;
            };
            document.querySelectorAll(`a[data-support-dialog="${dialog.id}"]`).forEach(link => {
                link.addEventListener('click', event => {
                    if (event.button !== 0 || event.ctrlKey || event.metaKey || event.shiftKey || event.altKey) return;
                    event.preventDefault();
                    if (dialog.open) return;
                    dialogs.filter(other => other !== dialog && other.open).forEach(other => other.close());
                    opener = link;
                    dialog.showModal();
                    document.body.classList.add('support-dialog-open');
                });
            });
            dialog.addEventListener('pointerdown', event => { startedOutside = event.target === dialog && outside(event); });
            dialog.addEventListener('click', event => {
                if (startedOutside && event.target === dialog && outside(event)) dialog.close();
                startedOutside = false;
            });
            dialog.addEventListener('close', () => {
                if (!dialogs.some(other => other.open)) document.body.classList.remove('support-dialog-open');
                if (opener && opener.isConnected) opener.focus({preventScroll: true});
            });
        });
    }
    const feedback = document.getElementById('consultaConfirmada') || document.querySelector('.support-form-summary');
    if (feedback) feedback.focus();
})();
