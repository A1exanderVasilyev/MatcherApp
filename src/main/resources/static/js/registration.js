function previewProfilePhoto(input) {
    const preview = document.getElementById('profilePhotoPreview');
    const file = input.files[0];

    if (file) {
        // Валидация размера файла
        if (file.size > 5 * 1024 * 1024) {
            alert('Файл слишком большой. Максимальный размер: 5MB');
            input.value = '';
            preview.style.display = 'none';
            return;
        }

        const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
        if (!allowedTypes.includes(file.type)) {
            alert('Недопустимый тип файла. Разрешены: JPG, PNG, GIF, WebP');
            input.value = '';
            preview.style.display = 'none';
            return;
        }

        const reader = new FileReader();
        reader.onload = function(e) {
            preview.src = e.target.result;
            preview.style.display = 'block';
        }
        reader.readAsDataURL(file);
    } else {
        preview.style.display = 'none';
    }
}

function initDragAndDrop() {
    const uploadArea = document.querySelector('.upload-area');
    if (!uploadArea) return;

    ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
        uploadArea.addEventListener(eventName, preventDefaults, false);
    });

    function preventDefaults(e) {
        e.preventDefault();
        e.stopPropagation();
    }

    ['dragenter', 'dragover'].forEach(eventName => {
        uploadArea.addEventListener(eventName, highlight, false);
    });

    ['dragleave', 'drop'].forEach(eventName => {
        uploadArea.addEventListener(eventName, unhighlight, false);
    });

    function highlight() {
        uploadArea.style.borderColor = '#4a90e2';
        uploadArea.style.backgroundColor = '#f0f8ff';
    }

    function unhighlight() {
        uploadArea.style.borderColor = '#ccc';
        uploadArea.style.backgroundColor = '';
    }

    uploadArea.addEventListener('drop', handleDrop, false);

    function handleDrop(e) {
        const dt = e.dataTransfer;
        const files = dt.files;
        document.getElementById('profilePhoto').files = files;
        previewProfilePhoto(document.getElementById('profilePhoto'));
    }
}

// Функции для геолокации
function initGeolocation() {
    const locationBtn = document.getElementById('locationBtn');
    const locationStatus = document.getElementById('locationStatus');
    const latitudeInput = document.getElementById('latitude');
    const longitudeInput = document.getElementById('longitude');
    const cityInput = document.getElementById('city');
    const countryInput = document.getElementById('country');

    if (!locationBtn || !locationStatus) return;

    function getLocation() {
        locationBtn.disabled = true;
        locationBtn.textContent = 'Определяем...';
        locationStatus.className = 'location-status status-info';
        locationStatus.innerHTML = 'Определяем ваше местоположение...';

        if (!navigator.geolocation) {
            showError('Геолокация не поддерживается вашим браузером');
            return;
        }


        navigator.geolocation.getCurrentPosition(
            function(position) {
                const lat = position.coords.latitude;
                const lon = position.coords.longitude;

                if (latitudeInput) latitudeInput.value = lat;
                if (longitudeInput) longitudeInput.value = lon;

                console.log('Coordinates received:', lon, lat);

                locationStatus.className = 'location-status status-success';
                locationStatus.innerHTML = '✅ Местоположение успешно определено!';

                // Автоматически заполняем город и страну через обратное геокодирование
                reverseGeocode(lat, lon);

                locationBtn.disabled = false;
                locationBtn.textContent = '📍 Обновить местоположение';
            },

            function(error) {
                let errorMessage = 'Не удалось определить местоположение. ';

                switch(error.code) {
                    case error.PERMISSION_DENIED:
                        errorMessage += 'Вы отказали в доступе к геолокации.';
                        break;
                    case error.POSITION_UNAVAILABLE:
                        errorMessage += 'Информация о местоположении недоступна.';
                        break;
                    case error.TIMEOUT:
                        errorMessage += 'Время ожидания истекло.';
                        break;
                    default:
                        errorMessage += 'Произошла неизвестная ошибка.';
                }

                showError(errorMessage);
            },

            {
                enableHighAccuracy: true,
                timeout: 15000,
                maximumAge: 60000
            }
        );
    }

    function showError(message) {
        if (!locationStatus) return;

        locationStatus.className = 'location-status status-error';
        locationStatus.innerHTML = message;
        if (locationBtn) {
            locationBtn.disabled = false;
            locationBtn.textContent = '📍 Попробовать снова';
        }
    }

    function reverseGeocode(lat, lon) {
        fetch(`https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lon}`)
            .then(response => response.json())
            .then(data => {
                if (data.address) {
                    const city = data.address.city || data.address.town || data.address.village;
                    const country = data.address.country;

                    if (city && cityInput && !cityInput.value) {
                        cityInput.value = city;
                    }
                    if (country && countryInput && !countryInput.value) {
                        countryInput.value = country;
                    }

                    if (locationStatus) {
                        locationStatus.innerHTML += `<br> Автоматически определено: ${city || 'неизвестный город'}, ${country || 'неизвестная страна'}`;
                    }
                }
            })
            .catch(error => {
                console.log('Reverse geocoding failed:', error);
            });
    }


    window.getLocation = getLocation;
    window.showError = showError;

    document.addEventListener('DOMContentLoaded', function() {
        setTimeout(() => {
            getLocation();
        }, 1000);
    });
}


document.addEventListener('DOMContentLoaded', function() {
    initDragAndDrop();
    initGeolocation();

    window.previewProfilePhoto = previewProfilePhoto;
});

document.addEventListener('DOMContentLoaded', function() {
    const uploadArea = document.querySelector('.upload-area');
    if (uploadArea) {
        uploadArea.addEventListener('click', function() {
            document.getElementById('profilePhoto').click();
        });
    }
});
