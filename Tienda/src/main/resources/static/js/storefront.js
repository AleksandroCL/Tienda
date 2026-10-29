(() => {
    const navigation = document.querySelector('.site-nav');
    const toggle = document.getElementById('navToggle');
    const menu = document.getElementById('navMenu');
    const close = document.getElementById('navClose');
    const backdrop = document.getElementById('navBackdrop');
    const mobile = window.matchMedia('(max-width: 1080px)');

    if (navigation && toggle && menu && close && backdrop) {
        let isOpen = false;
        const setOpen = (value, restoreFocus = false) => {
            isOpen = value && mobile.matches;
            navigation.classList.toggle('is-open', isOpen);
            document.body.classList.toggle('navigation-open', isOpen);
            toggle.setAttribute('aria-expanded', String(isOpen));
            toggle.setAttribute('aria-label', isOpen ? 'Cerrar menú' : 'Abrir menú');
            menu.inert = mobile.matches && !isOpen;
            backdrop.hidden = !isOpen;
            if (isOpen) {
                menu.setAttribute('role', 'dialog');
                menu.setAttribute('aria-modal', 'true');
                menu.setAttribute('aria-label', 'Menú principal');
                // Esperar a que el panel deje de estar oculto antes de enfocarlo.
                requestAnimationFrame(() => {
                    if (isOpen) close.focus();
                });
            } else {
                menu.removeAttribute('role');
                menu.removeAttribute('aria-modal');
                menu.removeAttribute('aria-label');
                if (restoreFocus) toggle.focus();
            }
        };

        navigation.dataset.enhanced = 'true';
        toggle.hidden = false;
        toggle.addEventListener('click', () => setOpen(!isOpen, isOpen));
        close.addEventListener('click', () => setOpen(false, true));
        backdrop.addEventListener('click', () => setOpen(false, true));
        menu.querySelectorAll('a').forEach(link => link.addEventListener('click', () => setOpen(false)));
        mobile.addEventListener('change', () => setOpen(false));
        document.addEventListener('keydown', event => {
            if (!isOpen) return;
            if (event.key === 'Escape') {
                event.preventDefault();
                setOpen(false, true);
            }
            if (event.key === 'Tab') {
                const focusable = [...menu.querySelectorAll('a[href], button:not([disabled]), input:not([type="hidden"])')]
                    .filter(element => element.getClientRects().length > 0);
                const first = focusable[0];
                const last = focusable[focusable.length - 1];
                if (event.shiftKey && document.activeElement === first) {
                    event.preventDefault();
                    last.focus();
                } else if (!event.shiftKey && document.activeElement === last) {
                    event.preventDefault();
                    first.focus();
                }
            }
        });
        setOpen(false);
    }

    // En escritorio los filtros permanecen abiertos; sin JS siguen disponibles.
    const filters = document.getElementById('catalogFilters');
    if (filters) {
        const narrow = window.matchMedia('(max-width: 700px)');
        const updateFilters = () => {
            const query = new URLSearchParams(window.location.search);
            const hasFilters = ['buscar', 'categoriaId', 'plataformaId'].some(key => query.get(key));
            filters.open = !narrow.matches || hasFilters;
        };
        filters.querySelector('summary').addEventListener('click', event => {
            if (!narrow.matches) event.preventDefault();
        });
        narrow.addEventListener('change', updateFilters);
        updateFilters();
    }

    // Contemplar también imágenes cuyo error ocurrió antes de cargar este script.
    const useFallback = img => {
        if (!(img instanceof HTMLImageElement) || !img.dataset.fallback || img.dataset.fallbackApplied) return;
        img.dataset.fallbackApplied = 'true';
        img.src = img.dataset.fallback;
    };
    document.addEventListener('error', event => useFallback(event.target), true);
    document.querySelectorAll('img[data-fallback]').forEach(img => {
        if (img.complete && img.naturalWidth === 0) useFallback(img);
    });
})();
