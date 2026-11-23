# Proyecto muli-hilo
Simulador de un pool de minado con IU

## Descripción

Este proyecto implementa un sistema cliente-servidor donde:
- Un **servidor** genera bloques de transacciones y coordina el trabajo
- Múltiples **clientes** (mineros) buscan el nonce correcto para validar el bloque
- El primero en encontrar la solución notifica al servidor y todos los demás se detienen

## Funcionalidades

- Generación de paquetes de datos (transacciones)
- Conexión/desconexión de clientes al servidor
- Gestión concurrente de múltiples clientes
- Búsqueda y envío de soluciones por parte de los clientes
- Validación de soluciones en el servidor
- **UI para clientes** - Muestra logs y barra de progreso
- **UI para servidor** - Muestra conexiones y soluciones encontradas
- **Minería concurrente** - Cada cliente procesa rangos en paralelo
- **Configuración de dificultad** - Cuántos ceros debe tener el hash



