document.addEventListener("DOMContentLoaded", function () {
  const cargarClientes = async () => {
    try {
      const res = await fetch('http://localhost:8080/Test4/clientesv1', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + Cookies.get('token')
        }
      });

      if (!res.ok) throw new Error('Error HTTP: ' + res.status);

      const clientes = await res.json();
      console.log(clientes);
      const tbody = document.querySelector("#tablaClientes tbody");

      clientes.data.forEach(cli => {
        const fechaFormateada = moment(cli.fchNacCli, 'ddd MMM DD HH:mm:ss zzz YYYY').format('DD/MM/YYYY');
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${cli.codCli}</td>
            <td>${cli.dniCli}</td>
            <td>${cli.apaCli}</td>
            <td>${cli.amaCli}</td>
            <td>${cli.nomCli}</td>
            <td>${fechaFormateada}</td>
            <td>${cli.logiCli}</td>
          `;
        tbody.appendChild(row);
      });
    } catch (error) {
      console.error("Error al cargar los clientes:", error);
    }
  };

  cargarClientes();
});