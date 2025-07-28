# Biblomnemon – Book Reading Tracker

**Biblomnemon** is an Android application designed for book lovers to track their reading history and 
goals in an elegant and modern interface, with optional **Google Drive sync**.
The app is built using **Kotlin**, **Jetpack Compose**, and follows modern architectural patterns.

## ✨ Features

- 📖 **Personal Library Management**
  Save, browse, and manage your books with details like title, author, categories, and cover art.

- 🔍 **Search & Discover**
  Search books via Google Books and Open Library APIs including ISBN scanner to find books instantly.

- 📚 **Track Your Reading Timeline**  
  Monitor what you've read, how much you've progressed, and revisit your favorite books.

- 📈 **Daily and Weekly Reading Goals**  
  Visualize your performance through progress bars and indicators.

- 🤝 **Connect with Others via QR Codes**  
  Share your profile or scan others' QR codes to connect and follow each other.

- 🔄 **Google Drive Sync**  
  Keep your data safe and accessible across devices.

- 🌓 **Light and Dark Theme Support**  
  Automatically adapts to your system settings.

- 🔐 **App Security**
  App integrity and security checks.

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM, Repository Pattern, Adapter Pattern
- **DI Framework**: Koin
- **Networking**: [Retrofit](https://square.github.io/retrofit/) + Moshi
- **Storage**: Room, DataStorage, Google Drive
- **Barcode Scanner**: CameraX + ML Kit
- **QR Code**: ZXing / ML Kit
- **Logging**: Timber
- **Build Tools**: AGP 8.11.1, Kotlin KSP

## 📁 Project Structure


## 🎨 Design

- **Typography**: Inter (Regular & Bold)
- **Color Palette**: Elegant, warm theme with color tones, and soft surface layers.
- **UI Elements**: Custom chips, circular icons, themed buttons, cards, and progress indicators.
- **Design Preview**: See `ThemeShowcase.kt` for full UI component showcase (Light and Dark Modes).

## 🚧 Roadmap

- [x] Initial Jetpack Compose UI setup
- [x] Remote API integration for book services
- [x] Room database for local storage
- [x] ISBN barcode scanning (CameraX & ML Kit)
- [ ] Reading session tracking
- [ ] Profile QR sharing
- [ ] User authentication
- [ ] Google Drive Sync
- [ ] AI-based reading analytics

## 💡 Name Meaning

> *Biblomnēmon* (βιβλομνήμων) — "Book memory keeper", from ancient Greek roots **βίβλος** (book) + **μνήμων** (mindful, remembering).

## 📄 License

MIT License © 2025 Michal Schejbal.

## 📝 Notes

**Created with ❤️ by [Ginoskos](https:///ginoskos.com)** – Read, Learn, Remember.
