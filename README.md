# Transferencia de archivos con clave simetrica
## Desarrolladores
- Miguel Andres Sarasti
- Luisa Fernanda Quintero Fernández
- Alexander Samacá Burbano
## ¿Como hicieron el programa?
Primero hicimos el bosquejo de la implementación de una estructura cliente-servidor, con sus puertos y sus sockets. Luego comenzamos a revisar la libreria propia de java security donde ibamos a usar la mayoria de los metodos o implementaciones para el alcance de este proyecto. Luego investigamos como realizar el SHA-256 y el algoritmo Diffie-Hellman, después de eso se realiza la negociación de la clave, dicha clave tiene 256 bytes pero como se utiliza AES-128 se utilizan entonces solo 16 bytes. para la clave negociada, de igual manera se utiliza este mismo AES para encriptar y desencriptar.
## Dificultades
Las dificultades que encontramos mientras realizabamos el proyecto fue de como conectar un proyecto corriendo en simultaneo ya en una red,
además de que no teniamos una idea clara de como realizar el SHA-256, por lo cual tuvimos que investigar en internet para realizarlo con la libreria propia
de java security. <br>
También tuvimos problemas con el plantamiento del Diffie-Hellman porque no sabiamos bien como empezar la logica desde el cliente, ya que este es el que negocia la clave. Tuvimos que leer sobre como funciona esa implementación en Java.

## Conclusiones
- Gracias a este proyecto tuvimos un mayor acercamiento(y uno más real) al uso de algoritmos para comunicación segura, aplicando lo aprendido sobre AES y Diffie-Hellman en un proyecto cliente-servidor, y también aprendimos que gracias al cifrado, se obtiene una mayor seguridad durante las conversaciones y/o transferencias de archivos.
- Tambien nos dimos cuenta que hacer una aplicación segura puede llegar a ser bastante complejo incluso para aplicaciones mas simples, por lo que una implementación completa en la web o en un celular, podria resultar muy laboriosa.
