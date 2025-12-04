package com.example.soscloud.ui.common; // Kendi paket adınızla değiştirin

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebSettings; // WebSettings import edildi

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.soscloud.R;

public class FirstAidInfoBottomSheet extends BottomSheetDialogFragment {

    private static final String HTML_CONTENT = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <title>Temel İlk Yardım Bilgileri</title>\n" +
            "    <script src=\"https://cdn.tailwindcss.com\"></script>\n" +
            "    <style>\n" +
            "        /* Custom CSS for scrollable area */\n" +
            "        .scrollable-content {\n" +
            "            max-height: 0; /* Initially hidden */\n" +
            "            overflow-y: hidden; /* Hide scrollbar when hidden */\n" +
            "            transition: max-height 0.5s ease-in-out; /* Smooth transition */\n" +
            "        }\n" +
            "\n" +
            "        .scrollable-content.open {\n" +
            "            max-height: 500px; /* Max height when open - adjust as needed */\n" +
            "            overflow-y: auto; /* Enable vertical scrolling when open */\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body class=\"bg-gray-100 p-4 font-sans\">\n" +
            "\n" +
            "    <div class=\"container mx-auto bg-white rounded-lg shadow-lg p-6\">\n" +

            "\n" +
            "        <div id=\"ilkYardimContent\" class=\"scrollable-content mt-4 border-t border-gray-200 pt-4\">\n" +
            "        <h1 class=\"text-2xl font-bold mb-4 text-center text-gray-800\">Temel İlk Yardım Bilgileri</h1>\n" +
            "            <h2 class=\"text-xl font-semibold mb-3 text-gray-700\">İlk Yardım Nedir?</h2>\n" +
            "            <p class=\"mb-4 text-gray-600\">Herhangi bir kaza ya da yaşamı tehlikeye düşüren bir durumda, sağlık görevlilerinin tıbbi yardımı sağlanıncaya kadar, hayatın kurtarılması ya da durumun daha kötüye gitmesini önleyebilmek amacıyla olay yerinde, tıbbi araç gereç aranmaksızın mevcut araç ve gereçlerle yapılan ilaçsız uygulamalardır.</p>\n" +
            "\n" +
            "            <h2 class=\"text-xl font-semibold mb-3 text-gray-700\">İlk Yardımın Amaçları</h2>\n" +
            "            <p class=\"mb-4 text-gray-600\">Yaşamsal fonksiyonların sürdürülmesini sağlamak, Hasta/yaralının durumunun kötüleşmesini engellemek, İyileştirmeyi kolaylaştırmak ilk yardımın öncelikli amaçlarıdır.</p>\n" +
            "\n" +
            "            <h2 class=\"text-xl font-semibold mb-3 text-gray-700\">İlk Yardım Temel Uygulamaları (KBK)</h2>\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Koruma</h3>\n" +
            "            <p class=\"mb-4 text-gray-600\">Olay yerinde olası tehlikeleri belirleyerek güvenli bir çevre oluşturmaktır.</p>\n" +
            "            <ul class=\"list-disc list-inside mb-4 text-gray-600\">\n" +
            "                <li>Kazaya uğrayan araç mümkünse güvenli bir alana alınmalıdır</li>\n" +
            "                <li>Olay yeri görünebilir biçimde işaretlenmelidir</li>\n" +
            "                <li>Meraklı kişiler olay yerinden uzaklaştırılmalıdır</li>\n" +
            "                <li>Kazaya uğrayan aracın kontak anahtarı kapatılmalıdır</li>\n" +
            "                <li>Sigara içilmemelidir ve içilmesine izin verilmemelidir</li>\n" +
            "            </ul>\n" +
            "\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Bildirme</h3>\n" +
            "            <p class=\"mb-4 text-gray-600\">En hızlı şekilde gerekli yardım kuruluşlarına (112) haber verilmesidir. 112 arandığında kesin yer ve adres, kim, hangi numaradan arıyor, olayın tanımı, hasta ya da yaralı sayısı, durumu, nasıl bir yardım aldıkları açıklanmalıdır.</p>\n" +
            "\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Kurtarma</h3>\n" +
            "            <p class=\"mb-4 text-gray-600\">Olay yerinde hasta yaralılara müdahale; hızlı ancak sakin ve bilinçli bir şekilde yapılmalıdır. Hasta/yaralının durumunun değerlendirilmesine bağlı olarak ilk yardım yapılmalıdır. Eğer ilk yardım bilinmiyorsa asla hasta/yaralıya dokunulmamalı ve kımıldatılmamalıdır.</p>\n" +
            "\n" +
            "            <h2 class=\"text-xl font-semibold mb-3 text-gray-700\">Hasta/Yaralının Değerlendirilmesi (ABC)</h2>\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">A (Airway) - Hava Yolu Açıklığının Değerlendirilmesi</h3>\n" +
            "            <p class=\"mb-4 text-gray-600\">Hasta/yaralının ağız içi kontrol edilerek yabancı cisim varsa işaret parmağı ile çıkarılır. Bir el hasta/yaralının alnına, diğer elin 2 parmağı çene kemiğinin üzerine konulur, alından bastırılarak baş geriye doğru itilip Baş geri-Çene yukarı pozisyonu verilir.</p>\n" +
            "\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">B (Breathing) - Solunumun Değerlendirilmesi</h3>\n" +
            "            <p class=\"mb-4 text-gray-600\">Hasta/yaralının solunum yapıp yapmadığı Bak-Dinle-Hisset yöntemiyle 10 sn. süreyle değerlendirilir.</p>\n" +
            "\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">C (Circulation) - Dolaşımın Değerlendirilmesi</h3>\n" +
            "            <p class=\"mb-4 text-gray-600\">Bilinci kapalı ve solunumu varsa; hasta/yaralının nabzı değerlendirilir; Yetişkin ve çocuklarda şah damardan, 3 parmak ile 5 saniye kontrol edilir. Bebeklerde kol atardamarından, 3 parmak ile 5 saniye kontrol edilir.</p>\n" +
            "\n" +
            "            <h2 class=\"text-xl font-semibold mb-3 text-gray-700\">Temel Yaşam Desteği (TYD)</h2>\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Solunum Durması</h3>\n" +
            "            <p class=\"mb-4 text-gray-600\">Solunum hareketlerinin durması nedeniyle vücudun yaşamak için ihtiyacı olan oksijenden yoksun kalmasıdır. Hemen yapay solunuma başlanmaz ise bir süre sonra kalp durması meydana gelir.</p>\n" +
            "\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Kalp Durması</h3>\n" +
            "            <p class=\"mb-4 text-gray-600\">Bilinci kapalı kişide kalp atımının olmaması durumudur. Kalp durmasına en kısa sürede müdahale edilmezse dokuların oksijenlenmesi bozulacağı için beyin hasarı oluşur. Kişide solunumun olmaması, bilincin kapalı olması, hiç hareket etmemesi ve uyaranlara cevap vermemesi kalp durmasının belirtisidir.</p>\n" +
            "\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Yetişkinlerde Temel Yaşam Desteği</h3>\n" +
            "            <ul class=\"list-disc list-inside mb-4 text-gray-600\">\n" +
            "                <li>Kendinin ve çevrenin güvenliğini sağla</li>\n" +
            "                <li>Hastanın bilincini kontrol et (omuzlarından sarsarak \"İyi misiniz?\" diye sor)</li>\n" +
            "                <li>Bilinç yoksa yardım çağır (112)</li>\n" +
            "                <li>Hasta/yaralıyı sert bir zemine sırt üstü yatır</li>\n" +
            "                <li>Hava yolunu aç (baş geri-çene yukarı pozisyonu)</li>\n" +
            "                <li>Solunumu kontrol et (Bak-Dinle-Hisset yöntemiyle, 10 saniye)</li>\n" +
            "                <li>Solunum yoksa:</li>\n" +
            "                <li>Göğüs kemiğinin ortasına iki elin üst üste yerleştir</li>\n" +
            "                <li>Dirsekler kilitli, vücut ağırlığını kullanarak dik açıyla baskı uygula</li>\n" +
            "                <li>Dakikada 100-120 ritimle, 5-6 cm derinliğinde 30 baskı uygula</li>\n" +
            "                <li>Her baskıdan sonra göğüs kafesinin tamamen geri yükselmesine izin ver</li>\n" +
            "                <li>30 kalp masajından sonra 2 suni solunum uygula (eğitimli isen)</li>\n" +
            "                <li>Yardım gelene kadar veya hasta tepki verene kadar devam et</li>\n" +
            "            </ul>\n" +
            "\n" +
            "            <h2 class=\"text-xl font-semibold mb-3 text-gray-700\">Solunum Yolu Tıkanıklıkları</h2>\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Solunum Yolu Tıkanıklığı Nedir?</h3>\n" +
            "            <p class=\"mb-4 text-gray-600\">Solunum yolunun, solunumu gerçekleştirmesi için gerekli havanın geçmesine engel olacak şekilde tıkanmasıdır.</p>\n" +
            "\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Kısmi Tıkanma</h3>\n" +
            "            <p class=\"mb-4 text-gray-600\">Az da olsa, bir miktar hava geçişinin olduğu duruma kısmi tıkanma denir. Kişi öksürür, nefes alabilir, konuşabilir. İlk yardım olarak kişiye dokunulmaz ve öksürmeye teşvik edilir.</p>\n" +
            "\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Tam Tıkanma</h3>\n" +
            "            <p class=\"mb-4 text-gray-600\">Hava girişinin tamamen engellendiği duruma ise tam tıkanma denir. Kişi nefes alamaz, acı çeker gibi ellerini boynuna götürür, konuşamaz, rengi morarmıştır.</p>\n" +
            "\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Bilinçli Yetişkinlerde Heimlich Manevrası</h3>\n" +
            "            <ul class=\"list-disc list-inside mb-4 text-gray-600\">\n" +
            "                <li>Kişinin arkasında dur</li>\n" +
            "                <li>Kollarını bel etrafında sar</li>\n" +
            "                <li>Bir elini yumruk yap ve baş parmak tarafı göbek ile göğüs kemiği arasına gelecek şekilde yerleştir</li>\n" +
            "                <li>Diğer elinle yumruğunu kavra</li>\n" +
            "                <li>Hızla ve kuvvetlice içeri ve yukarı doğru baskı uygula</li>\n" +
            "                <li>Yabancı cisim çıkana kadar veya kişi bilincini kaybedene kadar tekrarla</li>\n" +
            "            </ul>\n" +
            "\n" +
            "            <h2 class=\"text-xl font-semibold mb-3 text-gray-700\">Kanamalar</h2>\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Kanama Nedir?</h3>\n" +
            "            <p class=\"mb-4 text-gray-600\">Damar bütünlüğünün bozulması sonucu kanın damar dışına (vücut içine ya da dışına) çıkmasına kanama denir.</p>\n" +
            "\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Kanama Çeşitleri</h3>\n" +
            "            <ul class=\"list-disc list-inside mb-4 text-gray-600\">\n" +
            "                <li>Dış kanamalar: Kanın vücut dışına doğru akmasıdır.</li>\n" +
            "                <li>İç kanamalar: Kanın vücut içine akmasıdır.</li>\n" +
            "                <li>Doğal deliklerden olan kanamalar: Kulak, burun, ağız, anüs(makat) ve üreme organlarından olan kanamalardır.</li>\n" +
            "            </ul>\n" +
            "\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Dış Kanamalarda İlk Yardım</h3>\n" +
            "            <ul class=\"list-disc list-inside mb-4 text-gray-600\">\n" +
            "                <li>Yara üzerine direkt baskı yapılır,</li>\n" +
            "                <li>Kanama durmazsa ikinci bir bez konarak basınç arttırılır,</li>\n" +
            "                <li>Gerekirse bandaj ile sarılarak kanlanmış bezler kaldırılmadan basınç arttırılır,</li>\n" +
            "                <li>Kanayan bölgeye en yakın basınç noktasına bası uygulanır,</li>\n" +
            "                <li>Kanayan bölge yukarı kaldırılır,</li>\n" +
            "                <li>Kanama kol veya bacaklardaysa ve kırık şüphesi yoksa kanama bölgesini kalp hizasından yukarıda tutulur,</li>\n" +
            "                <li>Şok pozisyonu verilir,</li>\n" +
            "                <li>Sık sık yaşam bulguları kontrol edilir (2-3 dk. arayla).</li>\n" +
            "            </ul>\n" +
            "\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Burun Kanamasında İlk Yardım</h3>\n" +
            "            <ul class=\"list-disc list-inside mb-4 text-gray-600\">\n" +
            "                <li>Öncelikle sakin olunmalıdır.</li>\n" +
            "                <li>Hemen baş öne doğru eğilir. Mümkünse oturtulur.</li>\n" +
            "                <li>Burun kanatlarını sıkıştırarak iki parmakla sıkılır.</li>\n" +
            "                <li>Bu işlem yaklaşık 5 dakika kadar devam edilir.</li>\n" +
            "                <li>Kanamanın durmaması halinde hasta ya da yaralının en yakın sağlık kuruluşuna götürülmesi gerekir.</li>\n" +
            "            </ul>\n" +
            "\n" +
            "            <h2 class=\"text-xl font-semibold mb-3 text-gray-700\">Yanıklar</h2>\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Yanık Nedir?</h3>\n" +
            "            <p class=\"mb-4 text-gray-600\">Herhangi bir ısıya maruz kalma sonucu oluşan doku bozulmasıdır.</p>\n" +
            "\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Yanık Dereceleri</h3>\n" +
            "            <ul class=\"list-disc list-inside mb-4 text-gray-600\">\n" +
            "                <li>1. Derece yanıklar: Alt deride ve derinin yüzeyinde, kızarıklık, (pembe, kırmızı arası renk) şeklinde görülen doku hasarı vardır. Ağrı vericidir. Genelde 48 saatlik süreç içinde iyileşir.</li>\n" +
            "                <li>2. Derece yanıklar: Derinin 1. ve 2. tabakasını etkiler. En bariz özelliği deride içi su dolu kabarcıklar (bül) oluşmasıdır. Çok ağrılıdır.</li>\n" +
            "                <li>3. Derece yanıklar: Derinin tüm tabakaları etkilenir. Kaslar, sinirler, damarlar üzerinde etkilidir. Beyaz kuru yaradan, siyah renge kadar aşamaları vardır. Ağrısızdır, çünkü bütün sinirler zarar görmüştür.</li>\n" +
            "            </ul>\n" +
            "\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Isı İle Oluşan Yanıkta İlk Yardım</h3>\n" +
            "            <ul class=\"list-disc list-inside mb-4 text-gray-600\">\n" +
            "                <li>Kişi hala yanıyorsa, paniğe engel olunur, koşması engellenir.</li>\n" +
            "                <li>Hasta/yaralının battaniye vb. ile üzeri kapatılır ve yuvarlanması sağlanır.</li>\n" +
            "                <li>Hasta yaralının yaşamsal bulguları değerlendirilir.</li>\n" +
            "                <li>En az 20 dakika, soğuk su altında tutulur.</li>\n" +
            "                <li>Ödem oluşabileceğinden yüzük, bilezik vb. çıkarılır.</li>\n" +
            "                <li>Giysiler çıkarılır.</li>\n" +
            "                <li>Su toplayan yerler patlatılmaz.</li>\n" +
            "                <li>Yanık bölgelere birlikte bandaj yapılmaz.</li>\n" +
            "                <li>Yanık üzeri temiz bezle örtülür, yanık üzerine hiçbir madde sürülmez.</li>\n" +
            "                <li>Hasta/yaralı battaniye ile örtülür.</li>\n" +
            "                <li>Yanık geniş ve sağlık kurumu uzaksa, kusma yoksa bilinci açıksa hasta/yaralıya ağızdan sıvı verilerek sıvı kaybı engellenir.</li>\n" +
            "                <li>Tıbbi yardım istenir (112).</li>\n" +
            "            </ul>\n" +
            "\n" +
            "            <h2 class=\"text-xl font-semibold mb-3 text-gray-700\">Kırık, Çıkık ve Burkulmalar</h2>\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Kırık Nedir?</h3>\n" +
            "            <p class=\"mb-4 text-gray-600\">Kemik bütünlüğünün bozulmasıdır. Kırıklar, darbe sonucu ya da kendiliğinden oluşur.</p>\n" +
            "\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Kırık Çeşitleri</h3>\n" +
            "            <ul class=\"list-disc list-inside mb-4 text-gray-600\">\n" +
            "                <li>Kapalı kırık: Kemik bütünlüğü bozulmuştur. Ancak deri sağlamdır.</li>\n" +
            "                <li>Açık kırık: Deri bütünlüğü bozulmuştur. Kemik uçları dışarı çıkabilir, beraberinde kanama ve enfeksiyon riski taşırlar.</li>\n" +
            "                <li>Parçalı kırık: Kemik birden fazla yerden kırılmıştır.</li>\n" +
            "            </ul>\n" +
            "\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Kırık Belirtileri</h3>\n" +
            "            <ul class=\"list-disc list-inside mb-4 text-gray-600\">\n" +
            "                <li>Bölgenin hareket edilmesi ile artan yoğun ağrı,</li>\n" +
            "                <li>Şekil bozukluğu (Diğer sağlam organ ile karşılaştırılır),</li>\n" +
            "                <li>Bölgede ödem ve kanama sonucu morarma,</li>\n" +
            "                <li>İşlev kaybı,</li>\n" +
            "                <li>Hareketlerde kısıtlama,</li>\n" +
            "                <li>Şişlik.</li>\n" +
            "            </ul>\n" +
            "\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Kırıklarda İlk Yardım</h3>\n" +
            "            <ul class=\"list-disc list-inside mb-4 text-gray-600\">\n" +
            "                <li>Yaşamı tehdit eden başka yaralanma varsa ona öncelik verilmelidir.</li>\n" +
            "                <li>Hasta/yaralı hareket ettirilmemelidir.</li>\n" +
            "                <li>Ani hareketlerden kaçınılmalı, kırık yerine konulmaya çalışılmamalıdır.</li>\n" +
            "                <li>Kırık kolda ise; ödem oluşacağından yüzük, saat vb. eşyalar çıkartılmalıdır.</li>\n" +
            "                <li>Kırık olan bölgede hareketi önlemek gerekmektedir.</li>\n" +
            "                <li>Açık kırık varsa; tespitten önce yara üzeri temiz bir bezle kapatılmalıdır.</li>\n" +
            "                <li>Kırık olan bölge, bir üst ve bir alt eklemi de içine alacak şekilde karton, tahta vb. sert cisimle tespit edilmelidir.</li>\n" +
            "                <li>Tespit edilen bölge yukarıda tutularak dinlenmeye alınmalıdır.</li>\n" +
            "                <li>Kırık bölgede sık aralıklarla nabız, derinin rengi kontrol edilmelidir.</li>\n" +
            "                <li>Hasta sıcak tutulmalıdır.</li>\n" +
            "                <li>Tıbbi yardım sağlanmalıdır.</li>\n" +
            "            </ul>\n" +
            "\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Burkulma Nedir?</h3>\n" +
            "            <p class=\"mb-4 text-gray-600\">Eklem yüzeylerinin anlık olarak ayrılmasıdır</p>\n" +
            "\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Burkulmada İlk Yardım</h3>\n" +
            "            <ul class=\"list-disc list-inside mb-4 text-gray-600\">\n" +
            "                <li>Sıkıştırıcı bir bandajla burkulan eklem dolaşımı engellemeyecek şekilde tespit edilir.</li>\n" +
            "                <li>Şişliği azaltmak için bölge yukarı kaldırılır.</li>\n" +
            "                <li>Soğuk uygulama yapılır.</li>\n" +
            "                <li>Hareket ettirilmez.</li>\n" +
            "                <li>Uzun süre geçmiyorsa, tıbbi yardım sağlanır.</li>\n" +
            "            </ul>\n" +
            "\n" +
            "            <h2 class=\"text-xl font-semibold mb-3 text-gray-700\">Zehirlenmeler</h2>\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Zehirlenme Nedir?</h3>\n" +
            "            <p class=\"mb-4 text-gray-600\">Vücuda toksik maddenin girmesi sonucu normal fonksiyonların bozulmasıdır.</p>\n" +
            "\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Zehirlenme Yolları</h3>\n" +
            "            <ul class=\"list-disc list-inside mb-4 text-gray-600\">\n" +
            "                <li>Sindirim yolu ile: Ev veya bahçede kullanılan kimyasal maddeler, zehirli mantarlar, bozuk besinler, aşırı ilaç ve alkol alınmasıdır.</li>\n" +
            "                <li>Solunum yolu ile: Genellikle karbon monoksit gazı (tüp kaçakları, şofben, sobalar) lağım çukurunda biriken karbondioksit, klor, yapıştırıcılar, boyalar, ev temizleyicileri vb.</li>\n" +
            "                <li>Cilt yolu ile: Zehirli madde vücuda deri yoluyla girer. İlaç enjeksiyonu, zehirli bitkilere temas, zirai ilaçlar, zehirli hayvanların ısırması ve sokması sonucu oluşur.</li>\n" +
            "            </ul>\n" +
            "\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Sindirim Yolu İle Zehirlenmede İlk Yardım</h3>\n" +
            "            <ul class=\"list-disc list-inside mb-4 text-gray-600\">\n" +
            "                <li>Bilinç kontrolü yapılmalıdır.</li>\n" +
            "                <li>Sadece ağız zehirli maddeyle temas etmişse su ile çalkalanmalıdır.</li>\n" +
            "                <li>El ile temas etmişse el sabunlu su ile yıkanır.</li>\n" +
            "                <li>Yaşam bulguları değerlendirilir.</li>\n" +
            "                <li>Kusma, bulantı, ishal vb. belirtiler değerlendirilir.</li>\n" +
            "                <li>Özellikle yakıcı maddenin alındığı durumlarda hasta asla kusturulmaz!</li>\n" +
            "                <li>Bilinç kaybı varsa koma pozisyonu verilir.</li>\n" +
            "                <li>Üstü örtülür.</li>\n" +
            "                <li>112 aranılır.</li>\n" +
            "                <li>Zehir danışma merkezi 114'ü arayın</li>\n" +
            "            </ul>\n" +
            "\n" +
            "            <h2 class=\"text-xl font-semibold mb-3 text-gray-700\">Bilinç Bozuklukları</h2>\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Bayılma (Senkop)</h3>\n" +
            "            <p class=\"mb-4 text-gray-600\">Bayılma (Senkop); beyne giden kan akışının azalması sonucu oluşan kısa süreli, yüzeysel ve geçici bilinç kaybıdır.</p>\n" +
            "\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Bayılma Belirtileri</h3>\n" +
            "            <ul class=\"list-disc list-inside mb-4 text-gray-600\">\n" +
            "                <li>Baş dönmesi, baygınlık, yere düşme,</li>\n" +
            "                <li>Bacaklarda uyuşma, bilinçte bulanıklık,</li>\n" +
            "                <li>Yüzde solgunluk,</li>\n" +
            "                <li>Üşüme, terleme,</li>\n" +
            "                <li>Hızlı ve zayıf nabız.</li>\n" +
            "            </ul>\n" +
            "\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Bayılma Durumunda İlk Yardım</h3>\n" +
            "            <p class=\"mb-4 text-gray-600\">Kişi bayıldı ise;</p>\n" +
            "            <ul class=\"list-disc list-inside mb-4 text-gray-600\">\n" +
            "                <li>Etraftaki meraklılar uzaklaştırılır</li>\n" +
            "                <li>Hasta/yaralı sırt üstü yatırılır ve ayakları 30 cm yukarı kaldırılır</li>\n" +
            "                <li>Solunum yolu açıklığı kontrol edilir ve korunur</li>\n" +
            "                <li>Sıkan giysiler gevşetilir</li>\n" +
            "                <li>Kusma varsa yan pozisyonda tutulur.</li>\n" +
            "            </ul>\n" +
            "\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Şok</h3>\n" +
            "            <p class=\"mb-4 text-gray-600\">Dolaşım sisteminin yaşamsal organlara yeterince kan gönderememesi nedeniyle ortaya çıkan ve tansiyon düşüklüğü ile seyreden bir akut dolaşım yetmezliği durumudur.</p>\n" +
            "\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Şok Belirtileri</h3>\n" +
            "            <ul class=\"list-disc list-inside mb-4 text-gray-600\">\n" +
            "                <li>Kan basıncında düşme,</li>\n" +
            "                <li>Hızlı ve zayıf nabız,</li>\n" +
            "                <li>Hızlı ve yüzeysel solunum,</li>\n" +
            "                <li>Ciltte soğukluk, solukluk ve nemlilik,</li>\n" +
            "                <li>Endişe, huzursuzluk,</li>\n" +
            "                <li>Baş dönmesi,</li>\n" +
            "                <li>Dudak çevresinde solukluk ya da morarma,</li>\n" +
            "                <li>Susuzluk hissi,</li>\n" +
            "                <li>Bilinç seviyesinde azalma</li>\n" +
            "            </ul>\n" +
            "\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Şokta İlk Yardım</h3>\n" +
            "            <ul class=\"list-disc list-inside mb-4 text-gray-600\">\n" +
            "                <li>Hasta/yaralının endişe ve korkuları giderilir.</li>\n" +
            "                <li>Mümkün olduğunca temiz hava soluması sağlanır.</li>\n" +
            "                <li>Hava yolunun açıklığı sağlanır.</li>\n" +
            "                <li>Kanama varsa hemen durdurulur.</li>\n" +
            "                <li>Şok pozisyonu verilir.</li>\n" +
            "                <li>Hasta/yaralı sıcak tutulur.</li>\n" +
            "                <li>Gereksiz yere hasta/yaralı hareket ettirilmez.</li>\n" +
            "                <li>112 aranır.</li>\n" +
            "            </ul>\n" +
            "\n" +
            "            <h2 class=\"text-xl font-semibold mb-3 text-gray-700\">Hasta ve Yaralı Taşıma Teknikleri</h2>\n" +
            "            <h3 class=\"text-lg font-medium mb-2 text-gray-600\">Hasta/Yaralı Taşırken Dikkat Edilmesi Gereken Kurallar</h3>\n" +
            "            <ul class=\"list-disc list-inside mb-4 text-gray-600\">\n" +
            "                <li>Baş – boyun – gövde ekseni esas alınmalı,</li>\n" +
            "                <li>Ekip çalışması yapılmalı,</li>\n" +
            "                <li>Hasta/yaralıya yakın mesafede çalışılmalı,</li>\n" +
            "                <li>Daha uzun ve kuvvetli kas grupları kullanılmalı,</li>\n" +
            "                <li>Sırtın gerginliğini korumak için dizler kalçadan bükülmeli,</li>\n" +
            "                <li>Yerden destek alacak şekilde, her iki ayağı kullanma ve birini diğerinden öne yerleştirme,</li>\n" +
            "                <li>Omuzlar leğen kemiği hizasında tutulmalıdır,</li>\n" +
            "                <li>Ağırlık kaldırırken karın muntazam tutulup, kalça kasılmalıdır,</li>\n" +
            "                <li>Kalkarken ağırlığı kalça kaslarına verilmelidir,</li>\n" +
            "                <li>Yavaş ve düz adımlarla yürülmelidir (Adımlar omuzdan geniş olmamalı.),</li>\n" +
            "                <li>Ani dönme ve bükülmelerden kaçınılmalı hasta/yaralı az hareket ettirilmelidir.</li>\n" +
            "            </ul>\n" +
            "\n" +
            "            <p class=\"mb-4 text-gray-600\">Unutulmamalıdır ki, bu temel ilk yardım bilgileri hayat kurtarabilir, ancak gerçek acil durumlarda profesyonel tıbbi yardım almak her zaman önceliklidir. İlk yardım konusunda eğitim alarak bilgi ve becerilerinizi geliştirmeniz, acil durumlarda daha etkili müdahale etmenizi sağlayacaktır.</p>\n" +
            "\n" +
            "            </div>\n" +
            "    </div>\n" +
            "\n" +
            "    <script>\n" +
            "            ilkYardimContent.classList.toggle('open');\n" +
            "    </script>\n" +
            "\n" +
            "</body>\n" +
            "</html>";

    public static FirstAidInfoBottomSheet newInstance() {
        return new FirstAidInfoBottomSheet();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_first_aid_info, container, false);
        WebView webView = view.findViewById(R.id.webView);

        // WebView ayarlarını al
        WebSettings webSettings = webView.getSettings();
        // JavaScript'i etkinleştir (butonun çalışması için gerekli)
        webSettings.setJavaScriptEnabled(true);

        // HTML içeriğini WebView'a yükle
        // loadDataWithBaseURL kullanmak, harici kaynakların (Tailwind CSS gibi) doğru yüklenmesine yardımcı olur.
        webView.loadDataWithBaseURL(null, HTML_CONTENT, "text/html", "UTF-8", null);

        return view;
    }
}
