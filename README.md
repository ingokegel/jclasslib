# jclasslib bytecode editor

## Download

Installers can be downloaded from the [releases section](https://github.com/ingokegel/jclasslib/releases).

## Purpose

jclasslib bytecode editor is a tool that visualizes all aspects of compiled Java class files and the contained bytecode.
Many aspects of class files can be edited in the UI.
In addition, it contains a library that enables developers to read and write Java class files and bytecode.

![Main window](web/screenshots/main_light.png)

## Editing

All constant pool entries can be edited. Referenced constant pool entries can directly be edited from the context
menu that is shown by the "Edit" button of attributes and constant pool entries.

![Editing](web/screenshots/edit_constant_value.png)

Bytecode instructions can be edited from the context menu:

![Editing bytecode instructions](web/screenshots/edit_opcode_popup.png)

## UI

Dark mode can be enabled persistently by selecting "File->Dark mode" in the main menu. 

![Dark mode](web/screenshots/main_dark.png)

Several localizations are available and can be chosen in the UI:

![i18n](web/screenshots/language_switcher.png)

## IntelliJ IDEA plugin

There is a plugin for IntelliJ IDEA that can be installed via the plugin manager. The action in the "View menu" 
can be invoked when a Java, Groovy or Kotlin file is open in the editor.

![IntelliJ IDEA action](web/screenshots/ij_action.png)

The bytecode will be shown in the "jclasslib" tool window on the right hand side. Multiple class files can be open
at the same time.

![IntelliJ IDEA action](web/screenshots/ij_window.png)

## License

jclasslib bytecode editor is released under the [GPL, Version 2.0](https://www.gnu.org/licenses/gpl-2.0.html).
