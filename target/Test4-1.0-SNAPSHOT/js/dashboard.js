
// Toggle Sidebar
document.getElementById('toggle-sidebar').addEventListener('click', function () {
  document.getElementById('sidebar').classList.toggle('show');
  document.getElementById('main-content').classList.toggle('sidebar-collapsed');
});

// Dropdown Menu
document.getElementById('user-menu').addEventListener('click', function () {
  document.getElementById('user-dropdown').classList.toggle('show-dropdown');
});

// Close dropdown when clicking outside
document.addEventListener('click', function (event) {
  if (!event.target.closest('#user-menu')) {
    document.getElementById('user-dropdown').classList.remove('show-dropdown');
  }
});

// Submenu functionality
const hasDropdowns = document.querySelectorAll('.has-dropdown');
hasDropdowns.forEach(item => {
  item.addEventListener('click', function (e) {
    if (e.target.tagName === 'A') {
      e.preventDefault();
      this.classList.toggle('active');
      const dropdown = this.querySelector('.dropdown-menu');
      dropdown.classList.toggle('show');
    }
  });
});

// Simulated chart data (in a real app you would use Chart.js or similar)
function simulateChart() {
  console.log("Cargando datos del gráfico...");
  // Aquí iría la lógica para cargar un gráfico real
}

// Initialize
document.addEventListener('DOMContentLoaded', function () {
  const username = document.getElementById("username");
  username.textContent = Cookies.get("username"); // Fallback if cookie not found

  const btnGenerar = document.getElementById("generar-reporte");
  btnGenerar.addEventListener("click", async function () {
    try {
      // Mostrar loading (opcional)
      btnGenerar.disabled = true;
      btnGenerar.textContent = "Generando...";

      await generarReporte();

    } catch (error) {
      console.error('Error al generar el reporte:', error);
      alert('Error al generar el reporte: ' + error.message);
    } finally {
      // Restaurar botón
      btnGenerar.disabled = false;
      btnGenerar.textContent = "Generar Reporte";
    }
  });

  async function generarReporte() {
    try {
      // 1. Obtener el token (ajusta según cómo lo almacenes)
      const token = Cookies.get('token'); // Asumiendo que el token se guarda en una cookie

      if (!token) {
        throw new Error('No se encontró token de autenticación. Por favor, inicia sesión.');
      }

      // 2. Hacer la petición al servlet
      const response = await fetch('clientereporte', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      // 3. Verificar si la respuesta es exitosa
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || `Error ${response.status}: ${response.statusText}`);
      }

      // 4. Verificar que sea un PDF
      const contentType = response.headers.get('content-type');
      if (!contentType || !contentType.includes('application/pdf')) {
        throw new Error('La respuesta no es un archivo PDF válido');
      }

      // 5. Convertir a blob y abrir/descargar el PDF
      const pdfBlob = await response.blob();
      const pdfUrl = URL.createObjectURL(pdfBlob);

      // Opción 1: Abrir en nueva pestaña
      window.open(pdfUrl, '_blank');

      // Opción 2: Descargar automáticamente (descomenta si prefieres esto)
      // descargarPDF(pdfBlob, 'reporte-clientes.pdf');

      // Limpiar URL después de un tiempo
      setTimeout(() => {
        URL.revokeObjectURL(pdfUrl);
      }, 1000);

    } catch (error) {
      console.error('Error en generarReporte:', error);
      throw error; // Re-lanzar para que lo maneje el event listener
    }
  }

  //simulateChart();
});
