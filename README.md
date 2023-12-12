# Házi feladat specifikáció

Egy olyan telefon alkalmazást szeretnék létrehozni amely lehetővé teszi, hogy olyan zenéket tudjunk lejátszani lokálisan a Youtube-ról amik nem érhetőek el Spotify-on. Az ember nagyon gyakran ütközik olyan problémába, hogy a kedvenc zenéje vagy annak egy remixe nem érhető el a Spotify-on, így internet nélkül nem tudja bármikor lejátszani azt, ha csak nem veszi meg a Youtube Premium-ot (de ahhoz marha kell legyél). Erre szolgálna az én applikációm, aminek a segítségével le tudsz tölteni Youtube-ról zenét, majd rögtön az app-ban le is tudod az játszani. Így internet használata nélkül is lehet zenét hallgatni majd. Ennek megvalósítására a Youtube API-t fogom használni. 

## Bemutatás

Youtube és Spotify összekombinálása olyan felhasználok számára akik nem akarnak fizetni ezek javak érdekében. Az app lehetővé teszi Youtube zenék, akár videók és podcast-ek letöltését és egyidőben ezek meghallgatását. A hallgatás funkció elérhető lesz offline módban is.

## Főbb funkciók

Egy BottomNavBar segítségével lehet majd a két funkció között váltani. Ezek mind fragmenteken fognak megjelenni. Az elsőn fog szerepelni a YoutubeAPI-al lekért adatok egy RecyclerView segítségével lesznek megjelenítve. A letöltött zenéket a telefon tárhelyén fogjuk tárolni és a fontosabb információkat róla pedig egy SQLite adatbázisban. Majd pedig a következő fragmenten fog szerepelni a Playlist-ek. Harmadikon pedig a zenék, ugyan csak RecyclerView megjelenítésével. Egy speciólis UI segítségével pedig látszani fog az aktiálisan lejátszott zene státusza.

## Választott technológiák:

- API
- fragmentek
- RecyclerView
- SQLite
