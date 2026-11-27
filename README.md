# Paraules — demo Android

Aplicación de comunicación para tablet, hecha en Kotlin con Jetpack Compose y basada en las pantallas tablet del Figma.

## Abrir en Android Studio

1. Abre la carpeta `ParaulesAndroid` con **Open**.
2. Deja que Gradle sincronice el proyecto.
3. Ejecuta la configuración `app` en una tablet o emulador configurado en horizontal.

El proyecto usa Android Gradle Plugin 8.7.3, Gradle 9.6.0, Kotlin 2.2.10 y Android SDK 35. El `AndroidManifest.xml` fija la orientación horizontal y deja fuera pantallas pequeñas.

## Flujo

- **Començar** abre las categorías del diccionario catalán, que contiene 300 sustantivos en 16 grupos.
- Tocar una palabra la añade o la quita de la frase. Se pueden seleccionar hasta seis.
- La lista seleccionada se puede reordenar arrastrando el asa o usando las flechas; la cruz elimina cada palabra.
- **Copiar** copia la frase al portapapeles, **Netejar** borra la selección y **Finalitzar** abre el resultado.
- **Torna a jugar** vuelve al inicio para empezar otra frase.

Los controles tienen objetivos táctiles amplios y usan la tipografía Atkinson Hyperlegible del diseño. El diccionario original está en `app/src/main/assets/substantius_300_ca.json`.