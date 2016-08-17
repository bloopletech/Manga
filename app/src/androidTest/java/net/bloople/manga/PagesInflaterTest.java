package net.bloople.manga;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Created by i on 3/06/2016.
 */
@RunWith(AndroidJUnit4.class)
public class PagesInflaterTest {
    @Test
    public void inflater_inflate() {
        String[] originalPages = {"001.jpg", "002.jpg", "003.jpg", "004.jpg", "005.jpg", "006.jpg", "007.jpg", "008.jpg", "009.jpg", "010.jpg", "011_wide.jpg", "012.jpg", "013.jpg", "014.jpg", "015.jpg", "016.jpg", "017.jpg", "018.jpg", "019.jpg", "020.jpg", "021.jpg", "022.jpg", "023.jpg", "024.jpg", "025.jpg", "026.jpg", "027.jpg", "028.jpg", "029.jpg", "030.jpg", "031.jpg", "032.jpg", "033.jpg", "034.jpg", "035.jpg", "036.jpg", "037.jpg", "038.jpg", "039.jpg", "040.jpg", "041.jpg", "042.jpg", "043.jpg", "044.jpg", "045.jpg", "046.jpg", "047.jpg", "048.jpg", "049.jpg", "050.jpg", "051.jpg", "052.jpg", "053.jpg", "054.jpg", "055.jpg", "056.jpg", "057.jpg", "058.jpg", "059.jpg", "060.jpg", "061.jpg", "062.jpg", "063.jpg", "064.jpg", "065.jpg", "066.jpg", "067.jpg", "068.jpg", "069.jpg", "070.jpg", "071.jpg", "072.jpg", "073.jpg", "074.jpg", "075.jpg", "076.jpg", "077.jpg", "078.jpg", "079.jpg", "080.jpg", "081.jpg", "082.jpg", "083.jpg", "084.jpg", "085.jpg", "086.jpg", "087.jpg", "088.jpg", "089.jpg", "090.jpg", "091.jpg", "092.jpg", "093.jpg", "094.jpg", "095.jpg", "096.jpg", "097.jpg", "098.jpg", "099.jpg", "100.jpg", "101.jpg", "102.png", "103.jpg", "104.jpg", "105.jpg", "106.jpg", "107.jpg", "108.jpg", "109.jpg", "110.jpg", "111.jpg", "112.jpg", "113.jpg", "114.jpg", "115.jpg", "116.jpg", "117.jpg", "118.jpg", "119.jpg", "120.jpg", "121.jpg", "122.jpg", "123.jpg", "124.jpg", "125.jpg", "126.jpg", "127.jpg", "128.jpg", "129.jpg", "130.jpg", "131.jpg", "132.jpg", "133.jpg", "134.jpg", "135.jpg", "136.jpg", "137.jpg", "138.jpg", "139.jpg", "140.jpg", "141.jpg", "142.jpg", "143.jpg", "144.jpg", "145.jpg", "146.jpg", "147.jpg", "148.jpg", "149.jpg", "150.jpg", "151_credits.jpg", "152.jpg", "153.jpg", "154.jpg"};

        List<String> originalList = Arrays.asList(originalPages);

        PagesInflater inflater = new PagesInflater("001.jpg/9|011_wide.jpg|012.jpg/89|102.png|103.jpg/47|151_credits.jpg|152.jpg/2");
        ArrayList<String> list = inflater.inflate();
        assertEquals(list, originalList);
    }
}
