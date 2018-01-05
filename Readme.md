# Projekt 1 - Programowanie Obiektowe

Projekt wykorzystuje 2 biblioteki zewnętrzne (OpenSource):
[JSAP](http://www.martiansoftware.com/jsap/) - Parsowanie linii poleceń
[Roman Numerals](http://frequal.com/RomanNumerals/index.html) - Konwersja liczb arabskich na rzymskie.

Pliki ustaw "konstytucja.txt" i "uokik.txt" pochodzą z [lab8](https://github.com/apohllo/obiektowe-lab/tree/master/lab8).

Pierwszym argumentem programu jest ścieżka dostępu do pliku. 

Opcje: \
-h (--help) - Program wyświetla pomoc, a potem kończy działanie. \
-T (--table-of-contents) - Program wyświetla spis treści dokumentu zamiast treści. \
Jeżeli obecna jest opcja (-c \<numer sekcji>) program wyświetla tylko spis treści sekcji. \
-s (--section) \<numer działu> - Program wyświetla dział o ustalonym numerze, a potem kończy pracę. Jeżeli dokument nie zawiera działów opcja jest ignorowana. \
-c (--chapter) \<numer rozdziału> - Program wyświetla rozdział o ustalonym numerze. (Dla uokik należy sprecyzować numer działu za pomocą opcji -c) \
-A (--article-range) 'numer początku','numer końca' - Program wyświetla artykuły pomiędzy numerem początku, a numer końca włącznie, a potem kończy pracę. Kolejne numery rozdzielane są przecinkiem. Podanie nieparzystej liczby numerów powoduje odrzucenie ostatniego. \
-a (--article) \<numer artykułu> - Program wyświetla artykuł o zadanym numerze, a potem kończy pracę.

Jeżeli obecne są opcje wymienione powyżej to przetwarzane są w następującym priorytecie: \
h -> T -> s, c -> A > a \

Jeżeli brak wyżej wymienionych opcji, kolejnymi argumentami są specyficzne fragmenty artykułu. \
* art. \<numer artykułu> \
* ust. \<numer ustępu> \
* pkt. \<numer punktu> \
* lit. \<numer liter> \

Kolejne argumenty muszą rozdzielone być przecinkami. Niewłaściwie argumenty lub powtórzenia są ignorowane.