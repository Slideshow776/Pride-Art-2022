START /W gradlew desktop:dist
ECHO Back from gradlew desktop:dist

@RD /S /Q "C:\Users\Sandra Moen\dev\Trans Agent X\packr build files"
CD C:\Users\Sandra Moen\dev

java -jar packr-all-4.0.0.jar ^
--platform "windows64" ^
--jdk "C:\Program Files\Java\jdk-15.0.1" ^
--useZgcIfSupportedOs ^
--executable "desktop-1.0" ^
--classpath "C:\Users\Sandra Moen\dev\Trans Agent X\desktop\build\libs\Trans Agent X.jar" ^
--mainclass "no.sandramoen.prideart2022.desktop.DesktopLauncher" ^
--resources "C:\Users\Sandra Moen\dev\Trans Agent X\core\assets" ^
--output "C:\Users\Sandra Moen\dev\Trans Agent X\packr build files"

CD C:\Users\Sandra Moen\dev\Trans Agent X
