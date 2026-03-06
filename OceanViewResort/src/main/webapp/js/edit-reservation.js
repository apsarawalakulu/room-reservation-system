document.addEventListener('DOMContentLoaded', async () => {
    const urlParams = new URLSearchParams(window.location.search);
    const reservationId = urlParams.get('id');

    if (!reservationId) {
        document.getElementById('loading').style.display = 'none';
        document.getElementById('errorMsg').style.display = 'block';
        document.getElementById('errorMsg').textContent = 'Cannot edit; no valid Reservation ID parameter provided.';
        return;
    }

    const form = document.getElementById('reservationForm');
    const checkAvailabilityBtn = document.getElementById('checkAvailabilityBtn');
    const roomSelect = document.getElementById('roomSelect');
    const submitBtn = document.getElementById('submitBtn');
    const formError = document.getElementById('formError');
    const guestsCountInput = document.getElementById('guestsCount');

    let originalData = null;

    try {
        originalData = await Api.request(`/reservations/${reservationId}`);

        if (originalData.status === 'Cancelled') {
            document.getElementById('loading').style.display = 'none';
            document.getElementById('errorMsg').style.display = 'block';
            document.getElementById('errorMsg').textContent = 'Edits blocked systematically. Cannot edit a cancelled reservation due to ledger immutability standard rules.';
            return;
        }

        document.getElementById('loading').style.display = 'none';
        document.getElementById('editFormCard').style.display = 'block';
        document.getElementById('resNumTitle').textContent = originalData.reservationNumber;

        document.getElementById('fullName').value = originalData.guest.fullName;
        document.getElementById('contactNumber').value = originalData.guest.contactNumber;
        document.getElementById('email').value = originalData.guest.email;
        document.getElementById('address').value = originalData.guest.address;

        document.getElementById('checkIn').value = originalData.checkInDate;
        document.getElementById('checkOut').value = originalData.checkOutDate;
        guestsCountInput.value = originalData.numberOfGuests;
        document.getElementById('specialRequests').value = originalData.specialRequests || '';

        const opt = document.createElement('option');
        opt.value = originalData.roomNumber;
        opt.textContent = `Current: Room ${originalData.roomNumber} - ${originalData.room.roomType}`;
        roomSelect.appendChild(opt);

    } catch (e) {
        document.getElementById('loading').style.display = 'none';
        document.getElementById('errorMsg').style.display = 'block';
        document.getElementById('errorMsg').textContent = 'Failed to fetch the initial reservation form.';
        return;
    }

    checkAvailabilityBtn.addEventListener('click', async () => {
        const checkIn = document.getElementById('checkIn').value;
        const checkOut = document.getElementById('checkOut').value;
        const guests = parseInt(guestsCountInput.value);

        formError.textContent = '';

        if (!checkIn || !checkOut) {
            formError.textContent = 'Precise dates required.';
            return;
        }

        if (new Date(checkOut) <= new Date(checkIn)) {
            formError.textContent = 'Check-out date must naturally traverse after check-in date.';
            return;
        }

        try {
            checkAvailabilityBtn.textContent = 'Executing network search...';
            checkAvailabilityBtn.disabled = true;

            const rooms = await Api.request(`/rooms/available?checkIn=${checkIn}&checkOut=${checkOut}`);

            const isSameDates = (checkIn === originalData.checkInDate && checkOut === originalData.checkOutDate);

            const availableRoomsData = rooms.filter(r => r.capacity >= guests);

            roomSelect.innerHTML = '<option value="">-- Re-Select verified assignment --</option>';

            if (isSameDates && originalData.room.capacity >= guests) {
                const opt = document.createElement('option');
                opt.value = originalData.roomNumber;
                opt.textContent = `Current: Room ${originalData.roomNumber} - ${originalData.room.roomType}`;
                roomSelect.appendChild(opt);
            }

            availableRoomsData.forEach(r => {
                if (r.roomNumber !== originalData.roomNumber) {
                    const opt = document.createElement('option');
                    opt.value = r.roomNumber;
                    opt.textContent = `Room ${r.roomNumber} - ${r.roomType}`;
                    roomSelect.appendChild(opt);
                }
            });

            if (roomSelect.options.length === 1) {
                roomSelect.innerHTML = '<option value="">No valid capacity allocations existing in timeframe.</option>';
                roomSelect.disabled = true;
                submitBtn.disabled = true;
            } else {
                roomSelect.disabled = false;
                submitBtn.disabled = false;
            }

        } catch (e) {
            formError.textContent = e.data && e.data.error ? e.data.error : 'Failed DB inventory connection.';
        } finally {
            checkAvailabilityBtn.textContent = 'Re-check Available Rooms';
            checkAvailabilityBtn.disabled = false;
        }
    });

    function invalidateRoom() {
        roomSelect.disabled = true;
        submitBtn.disabled = true;
        roomSelect.innerHTML = '<option value="">Parameters mutated globally. Re-verify available constraints.</option>';
    }

    guestsCountInput.addEventListener('change', invalidateRoom);
    document.getElementById('checkIn').addEventListener('change', invalidateRoom);
    document.getElementById('checkOut').addEventListener('change', invalidateRoom);


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
            formError.textContent = 'Selection of constrained room required internally.';
            return;
        }

        try {
            submitBtn.textContent = 'Uploading Modifications...';
            submitBtn.disabled = true;

            await Api.request(`/reservations/${reservationId}`, {
                method: 'PUT',
                body: JSON.stringify(payload)
            });

            alert('Backend synchronized successfully!');
            window.location.href = `reservation-detail.html?id=${reservationId}`;

        } catch (e) {
            formError.textContent = e.data && e.data.error ? e.data.error : 'Network upload stream aborted.';
            submitBtn.textContent = 'Save Modifications';
            submitBtn.disabled = false;
        }
    });
});
