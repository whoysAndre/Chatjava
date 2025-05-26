document.getElementById("formCambiar").addEventListener("submit", async (e) => {
  e.preventDefault();
  const token = Cookies.get("token");
  const actual = document.getElementById("actual").value;
  const nueva = document.getElementById("nueva").value;
  const confirmar = document.getElementById("confirmar").value;
  const mensaje = document.getElementById("mensaje");

  mensaje.textContent = "";
  mensaje.className = "mensaje";

  if (nueva !== confirmar) {
    mensaje.textContent = "Las contraseñas no coinciden.";
    mensaje.classList.add("error");
    return;
  }

  const dataObject = {
    actual: actual,
    nueva: nueva
  };

  try {
    const response = await fetch("/api/cambiar-password", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}` //
      },
      body: JSON.stringify(dataObject)
    });

    const data = await response.json();

    if (response.ok) {
      mensaje.textContent = data.mensaje || "Contraseña actualizada con éxito.";
      mensaje.classList.add("exito");
      document.getElementById("formCambiar").reset();
    } else {
      mensaje.textContent = data.error || "Error al cambiar contraseña.";
      mensaje.classList.add("error");
    }

  } catch (error) {
    mensaje.textContent = "Error de red o del servidor.";
    mensaje.classList.add("error");
    console.error(error);
  }
});