# 📌 IOTRoom Manager Service

El **IOTRoom Manager** es un microservicio desarrollado en **Java 17, Spring Boot y Maven** que actúa como **puente entre Kafka, MQTT y el Sync Manager**.  
Su función principal es recibir comandos desde Kafka, validarlos, transformarlos al formato esperado por el **ESP8266 (NodeMCU)** y devolver un **acknowledgment (ack)** al **Sync Manager** para mantener la sincronización del flujo iniciado en **IOTRoomManager**.  

---

## 🔹 Flujo General

1. **Recepción desde Kafka**  
   - El Manager se suscribe al tópico `iot-commands`.  
   - Recibe un mensaje JSON con la instrucción para un GPIO del ESP8266.  

2. **Validación del Mensaje**  
   - Comprueba que los campos requeridos estén presentes (`operationType`, `payload.gpio`, `payload.status`).  
   - Verifica que los valores sean correctos (`status` solo puede ser `HIGH` o `LOW`).  
   - Si el mensaje es inválido, se descarta y se genera un log de error.  

3. **Publicación en MQTT**  
   - El mensaje válido se traduce al formato reducido que entiende el ESP8266.  
   - Se envía al tópico MQTT `iotroom/commands`.  
   - El ESP8266 recibe el comando y acciona el pin indicado.  

4. **Respuesta al Sync Manager**  
   - Una vez procesado el comando, el Manager construye un **ack**.  
   - Este ack puede enviarse vía **REST callback** (`POST /sync/ack`) o publicarse en un **tópico Kafka de respuestas** (ejemplo: `iot-acks`).  
   - El Sync Manager utiliza este ack para dar por cerrado el ciclo.  

---

## 🔹 Formato de Mensaje de Entrada (Kafka → Manager)

Ejemplo de comando recibido desde Kafka:

```json
{
  "operationType": "OUTPUT",
  "payload": {
    "status": "HIGH",
    "gpio": "GPIO_02",
    "isLowActive": false
  }
}
