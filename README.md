# BezbednostProj

Projekat obuhvata dve aplikacije koje omogućavaju  sigurnu razmenu e-mail poruka.



1. IBweb - Web aplikacija koja služi za distribuciju sertifikata.

- Potrebno je instalirati sertifikat koji se nalazi u data folderu "ib-web.cer", kako bi omogućili HTTPS komunikaciju.
- Pri pokretanju projekta otvoriće se login forma. Inicijalno postoje dva korisnika. 
  ADMIN - email:nemanjak@example.com pass:123
  REGULAR  - email:nikola@example.com pass:123
- Kada se korisnik registruje kreira se njegov jks file u data folderu aplikacije.
- Kada admin odobri novoregistrovanog korisnika, tek onda novoregistrovani korisnik može da se uloguje.
- Novoregistrovani korisnik kad izvrši login biće redirektovan na user.html stranicu gde ima mogućnost da izvrši preuzimanje svog jks file-a klikom na download dugme.

Keystore: "ib-web.jks", pass:123.

2. Mail Client V2 - Kljentska aplikacija koja pomoću kriptografije omogućava sigurnost e-mail poruke.

  -  Za ovu aplikaciju korišćeni su keystore-ovi i sertifikati generisani upotrebom alata Portecle. (usera.jks, userb.jks , password: 123).
  
  - Potrebno je pokrenuti klasu WriteMailClient i u konzoli je potrebno uneti primaoca poruke,subjekt i telo poruke.
  Prvo se kreira xml file, nakon toga se potpisuje i takođe čuva u xml formatu, potpisana poruka se enkriptuje i takođe čuva u xml formatu.
  Svaki file se može naći u data folderu. Pre slanja se pretvara u string i šalje korisniku u telu poruke.
  
  - Za dekripciju potrebno je pokrenuti klasu ReadMailClient. Iz liste mailova izabrati broj poruke koju želimo da dekriptujemo.
  Poruka se prvo preuzima u string formatu, nakon toga kreiramo xml fajl. Izvršavamo dekriptovanje poruke i verifikaciju potpisa.
  Ako je poruka uspešno dekriptovana i verifikovana, poruka će biti ispisana u konzoli.
  Biće prikazan slučaj i ako narušimo integritet poruke. U ovom slučaju potpis neće biti validan.
