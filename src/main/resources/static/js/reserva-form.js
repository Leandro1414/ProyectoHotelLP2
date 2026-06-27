document.addEventListener('DOMContentLoaded', () => {
    const ingreso = document.querySelector('#fechaIngreso');
    const salida = document.querySelector('#fechaSalida');
    const reservaId = document.querySelector('#idReserva');
    const roomContainer = document.querySelector('#roomContainer');
    if (!ingreso || !salida || !roomContainer) return;

    const selected = new Set(Array.from(roomContainer.querySelectorAll('input:checked')).map(i => Number(i.value)));

    async function cargarDisponibles() {
        roomContainer.querySelectorAll('input:checked').forEach(input => selected.add(Number(input.value)));
        if (!ingreso.value || !salida.value || salida.value <= ingreso.value) {
            roomContainer.innerHTML = '<div class="form-error">La fecha de salida debe ser posterior a la fecha de ingreso.</div>';
            return;
        }
        const params = new URLSearchParams({fechaIngreso: ingreso.value, fechaSalida: salida.value});
        if (reservaId && reservaId.value) params.set('reservaId', reservaId.value);
        roomContainer.innerHTML = '<div class="help">Consultando disponibilidad...</div>';
        try {
            const endpoint = roomContainer.dataset.availabilityUrl || '/reservas/api/habitaciones-disponibles';
            const response = await fetch(`${endpoint}?${params}`);
            if (!response.ok) throw new Error('No se pudo consultar la disponibilidad');
            const rooms = await response.json();
            if (rooms.length === 0) {
                roomContainer.innerHTML = '<div class="empty">No hay habitaciones disponibles para estas fechas.</div>';
                return;
            }
            roomContainer.innerHTML = rooms.map(room => `
                <label class="room-option">
                    <input type="checkbox" name="habitacionIds" value="${room.id}" ${selected.has(room.id) ? 'checked' : ''}>
                    <span>
                        <strong>Hab. ${escapeHtml(room.numero)} · ${escapeHtml(room.tipo)}</strong>
                        <span class="room-meta">Capacidad: ${room.capacidad} · S/ ${Number(room.precioNoche).toFixed(2)} por noche</span>
                    </span>
                </label>`).join('');
        } catch (error) {
            roomContainer.innerHTML = `<div class="form-error">${escapeHtml(error.message)}</div>`;
        }
    }

    function escapeHtml(value) {
        return String(value).replace(/[&<>'"]/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[c]));
    }

    ingreso.addEventListener('change', cargarDisponibles);
    salida.addEventListener('change', cargarDisponibles);
});
