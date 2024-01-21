## Лабораторная работа №3. Использование автоматических генераторов анализаторов Bison и ANTLR


### Вариант 5. Идеальное форматирование
Выберите подмножества языка C++, Java или Kotlin и напишите преобразование программы на заданном подмножестве этого языка в идеально отформатированную программу. Для Java следует использовать
Java Coding Conventions. Для C++ вы можете выбрать способ форматирования на свое усмотрение, например Google C++ Style Guide. Для
Kotlin можете выбрать разумный аналог.

[Tests](src/test/kotlin/ru/dfrolovd/TranslateTest.kt)<br/>
[Грамматика](src/main/antlr/ru/dfrolovd/Java.g4)<br/>
[Visitor](src/main/kotlin/ru/dfrolovd/Visitor.kt)

Запуск ```gradle run inFile outFile```