document.addEventListener('DOMContentLoaded', async () => {
    const urlParams = new URLSearchParams(window.location.search);
    const reservationId = urlParams.get('id');

    if (!reservationId) {
        document.getElementById('loading').style.display = 'none';
        document.getElementById('errorMsg').style.display = 'block';
        document.getElementById('errorMsg').textContent = 'Invalid state: No Reservation ID parameter explicitly provided in URL.';
        return;
    }

    try {
        const res = await Api.request(`/reservations/${reservationId}`);

        document.getElementById('loading').style.display = 'none';
        document.getElementById('detailContent').style.display = 'block';

        document.getElementById('resNumTitle').textContent = res.reservationNumber;

        const badge = document.getElementById('statusBadge');
        badge.textContent = res.status;
        if (res.status === 'Active') badge.className = 'status-badge status-active';
        else if (res.status === 'Cancelled') badge.className = 'status-badge status-cancelled';
        else badge.className = 'status-badge status-checkout';

        document.getElementById('guestName').textContent = res.guest.fullName;
        document.getElementById('guestContact').textContent = res.guest.contactNumber;
        document.getElementById('guestEmail').textContent = res.guest.email;
        document.getElementById('guestAddress').textContent = res.guest.address;

        document.getElementById('roomAlloc').textContent = `Room ${res.room.roomNumber} (${res.room.roomType})`;
        document.getElementById('stayDates').textContent = `${res.checkInDate} to ${res.checkOutDate}`;
        document.getElementById('occupancy').textContent = `${res.numberOfGuests} Guest(s)`;

        if (res.specialRequests) {
            document.getElementById('requests').textContent = res.specialRequests;
            document.getElementById('requests').style.fontStyle = 'normal';
        }

        if (res.status === 'Cancelled') {
            document.getElementById('cancelReasonGroup').style.display = 'block';
            document.getElementById('cancelReason').textContent = res.cancellationReason || 'N/A';

            document.getElementById('editBtn').style.display = 'none';
            document.getElementById('cancelBtn').style.display = 'none';
        }

        document.getElementById('editBtn').addEventListener('click', () => {
            window.location.href = `edit-reservation.html?id=${reservationId}`;
        });

        document.getElementById('billBtn').addEventListener('click', () => {
            window.location.href = `bill.html?id=${reservationId}`;
        });

        const modal = document.getElementById('cancelModal');
        document.getElementById('cancelBtn').addEventListener('click', () => {
            modal.style.display = 'block';
        });

        document.getElementById('closeModalBtn').addEventListener('click', () => {
            modal.style.display = 'none';
        });

        document.getElementById('confirmCancelBtn').addEventListener('click', async () => {
            const reason = document.getElementById('modalCancelReason').value.trim();
            if (!reason) {
                alert("Please provide a cancellation reason to satisfy DB constraints.");
                return;
            }
            try {
                await Api.request(`/reservations/${reservationId}/cancel`, {
                    method: 'PUT',
                    body: JSON.stringify({ cancellationReason: reason })
                });
                window.location.reload();
            } catch (e) {
                alert(e.data && e.data.error ? e.data.error : "Failed to cancel due to API error.");
            }
        });

    } catch (e) {
        document.getElementById('loading').style.display = 'none';
        document.getElementById('errorMsg').style.display = 'block';
        document.getElementById('errorMsg').textContent = e.data && e.data.error ? e.data.error : 'Failed to fetch reservation details externally.';
    }
});
