document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('reservationForm');
    const checkAvailabilityBtn = document.getElementById('checkAvailabilityBtn');
    const roomSelect = document.getElementById('roomSelect');
    const submitBtn = document.getElementById('submitBtn');
    const formError = document.getElementById('formError');
    const guestsCountInput = document.getElementById('guestsCount');
    let availableRoomsData = [];

    const today = new Date().toISOString().split('T')[0];
    document.getElementById('checkIn').setAttribute('min', today);
    document.getElementById('checkOut').setAttribute('min', today);

    checkAvailabilityBtn.addEventListener('click', async () => {
        const checkIn = document.getElementById('checkIn').value;
        const checkOut = document.getElementById('checkOut').value;
        const guests = parseInt(guestsCountInput.value);

        formError.textContent = '';

        if (!checkIn || !checkOut) {
            formError.textContent = 'Please select both check-in and check-out dates.';
            return;
        }

        if (new Date(checkOut) <= new Date(checkIn)) {
            formError.textContent = 'Check-out date must be strictly after check-in date.';
            return;
        }

        try {
            checkAvailabilityBtn.textContent = 'Searching Inventory...';
            checkAvailabilityBtn.disabled = true;

            const rooms = await Api.request(`/rooms/available?checkIn=${checkIn}&checkOut=${checkOut}`);
            availableRoomsData = rooms.filter(r => r.capacity >= guests);

            roomSelect.innerHTML = '';
            if (availableRoomsData.length === 0) {
                roomSelect.innerHTML = '<option value="">No rooms currently available matching this capacity.</option>';
                roomSelect.disabled = true;
                submitBtn.disabled = true;
            } else {
                roomSelect.innerHTML = '<option value="">-- Choose a room assignment --</option>';
                availableRoomsData.forEach(r => {
                    const opt = document.createElement('option');
                    opt.value = r.roomNumber;
                    opt.textContent = `Room ${r.roomNumber} - ${r.roomType} (Max Capacity: ${r.capacity}, LKR ${r.nightlyRate} / night)`;
                    roomSelect.appendChild(opt);
                });
                roomSelect.disabled = false;
                submitBtn.disabled = false;
            }

        } catch (e) {
            formError.textContent = e.data && e.data.error ? e.data.error : 'Failed to fetch room inventory over network.';
        } finally {
            checkAvailabilityBtn.textContent = 'Find Available Rooms';
            checkAvailabilityBtn.disabled = false;
        }
    });

    guestsCountInput.addEventListener('change', () => {
        if (roomSelect.disabled) return;
        roomSelect.disabled = true;
        submitBtn.disabled = true;
        roomSelect.innerHTML = '<option value="">Size capacity constraint modified. Please re-fetch rooms.</option>';
    });

    document.getElementById('checkIn').addEventListener('change', () => { roomSelect.disabled = true; submitBtn.disabled = true; roomSelect.innerHTML = '<option value="">Dates modified. Please re-fetch rooms.</option>'; });
    document.getElementById('checkOut').addEventListener('change', () => { roomSelect.disabled = true; submitBtn.disabled = true; roomSelect.innerHTML = '<option value="">Dates modified. Please re-fetch rooms.</option>'; });

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        formError.textContent = '';

        const payload = {
            fullName: document.getElementById('fullName').value.trim(),
            contactNumber: document.getElementById('contactNumber').value.trim(),
            email: document.getElementById('email').value.trim(),
            address: document.getElementById('address').value.trim(),
            checkInDate: document.getElementById('checkIn').value,
            checkOutDate: document.getElementById('checkOut').value,
            numberOfGuests: parseInt(guestsCountInput.value),
            roomNumber: roomSelect.value,
            specialRequests: document.getElementById('specialRequests').value.trim()
        };

        if (!payload.roomNumber) {
            formError.textContent = 'Please confirm room assignment before proceeding.';
            return;
        }

        try {
            submitBtn.textContent = 'Processing Transaction...';
            submitBtn.disabled = true;

            const res = await Api.request('/reservations', {
                method: 'POST',
                body: JSON.stringify(payload)
            });

            alert(`Reservation established! Trace ID: ${res.reservationNumber}`);
            window.location.href = `reservation-detail.html?id=${res.reservationNumber}`;

        } catch (e) {
            formError.textContent = e.data && e.data.error ? e.data.error : 'Network Error preventing transaction completion. Report to IT server admins.';
            submitBtn.textContent = 'Confirm Booking';
            submitBtn.disabled = false;
        }
    });
});
