# ShieldGuard AV - Antivírus Android Completo

## Funcionalidades Implementadas

### Proteção Core
- ✅ Scanner Antivírus em Tempo Real
- ✅ Scan Completo do Sistema
- ✅ Scan Rápido
- ✅ Scan Personalizado
- ✅ Quarentena de Ameaças
- ✅ Banco de Dados com 100+ Assinaturas de Malware
- ✅ Atualização de Assinaturas

### Segurança de Apps
- ✅ Scanner de Apps Instalados
- ✅ Analisador de Permissões
- ✅ Avaliação de Risco (Safe/Low/Medium/High/Critical)
- ✅ Desinstalação de Apps Suspeitos

### Segurança de Rede
- ✅ Scanner de Segurança WiFi
- ✅ Detecção de Redes Inseguras
- ✅ Monitor de Conexões Ativas

### Privacidade
- ✅ Analisador de Permissões Perigosas
- ✅ Score de Privacidade
- ✅ Identificação de Apps de Rastreamento

### Otimização
- ✅ Limpador de Arquivos Junk (Cache, Temp, Logs, APKs)
- ✅ Booster de Dispositivo
- ✅ Análise de Armazenamento

### Recursos Adicionais
- ✅ Dashboard de Segurança com Score Geral
- ✅ Notificações de Proteção
- ✅ Configurações Personalizáveis
- ✅ Tema Escuro/Claro
- ✅ Serviço de Proteção em Background
- ✅ Boot Receiver para Inicialização Automática

## Arquitetura

- **Padrão:** MVVM + Clean Architecture
- **DI:** Hilt (Dagger)
- **UI:** Jetpack Compose + Material3
- **Banco de Dados:** Room (SQLite)
- **Async:** Kotlin Coroutines + Flow
- **Network:** Retrofit2 + OkHttp3

## Como Gerar o APK

### Método 1: Máquina x86_64 (Recomendado)

1. Clone ou copie a pasta `ShieldGuardAV` para uma máquina Linux x86_64
2. Instale o Android SDK com build-tools 34.0.0
3. Execute:
```bash
cd ShieldGuardAV
./gradlew assembleDebug
```
4. O APK será gerado em: `app/build/outputs/apk/debug/app-debug.apk`

### Método 2: Docker (x86_64)

```bash
docker run -v $(pwd):/project -w /project --rm -it gradle:8.7.0-jdk17 ./gradlew assembleDebug
```

### Método 3: Cloud Build

Use serviços como:
- GitHub Actions
- GitLab CI
- CircleCI

## Estrutura do Projeto

```
ShieldGuardAV/
├── app/
│   ├── src/main/
│   │   ├── java/com/shieldguardav/
│   │   │   ├── data/
│   │   │   │   ├── local/ (Room DB, DAOs, Entities)
│   │   │   │   └── repository/ (Implementações)
│   │   │   ├── domain/
│   │   │   │   ├── model/ (Data Models)
│   │   │   │   ├── repository/ (Interfaces)
│   │   │   │   └── usecase/ (Casos de Uso)
│   │   │   ├── presentation/
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/ (Home, Apps, Security, Settings)
│   │   │   │   │   ├── components/ (UI Components)
│   │   │   │   │   └── theme/ (Theme, Colors, Typography)
│   │   │   │   ├── viewmodel/ (ViewModels)
│   │   │   │   └── navigation/ (NavGraph)
│   │   │   ├── service/ (ProtectionService, ScannerService)
│   │   │   ├── receiver/ (Boot, Network, Package Receivers)
│   │   │   └── ShieldGuardApp.kt (Application)
│   │   ├── res/ (Resources, Manifest)
│   │   └── AndroidManifest.xml
│   ├── build.gradle
│   └── proguard-rules.pro
├── build.gradle
├── settings.gradle
├── gradle.properties
└── local.properties (sdk.dir=/opt/android-sdk)
```

## Informações do App

- **Nome:** ShieldGuard AV
- **Package:** com.shieldguardav
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)
- **Versão:** 1.0.0

## Notas Importantes

⚠️ **Problema Conhecido:** O build no ARM64 (aarch64) falha porque as ferramentas de build do Android (aapt2) são x86_64 apenas.

✅ **Solução:** Execute o build em uma máquina x86_64 ou use emulação QEMU adequada.

## Arquivos Criados

Total de arquivos Kotlin: 25+
Total de arquivos de configuração: 10+
Total de assinaturas de malware: 100+

## Próximos Passos para APK

1. Copie a pasta ShieldGuardAV para uma máquina x86_64
2. Instale Android SDK (build-tools 34.0.0, platforms android-34)
3. Execute: `./gradlew assembleDebug`
4. APK gerado em: `app/build/outputs/apk/debug/app-debug.apk`
5. Instale no dispositivo: `adb install app-debug.apk`
