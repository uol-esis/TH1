package de.uol.pgdoener.th1.domain.infrastructure.converterchain.core.converter;

import de.uol.pgdoener.th1.application.dto.PivotMatrixStructureDto;
import de.uol.pgdoener.th1.domain.converterchain.model.converter.PivotMatrixConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PivotMatrixConverterTest {

    @Test
    void testWithEmptyListAndTwoCategoryRequest() {
        Map<String, List<Integer>> map = new LinkedHashMap<>();
        List<Integer> blockIndices = List.of(0);
        map.put("Kategorie", List.of(1, 8, 21));
        map.put("Unterkategorie", List.of());

        PivotMatrixStructureDto source = new PivotMatrixStructureDto()
                .pivotField(map)
                .blockIndices(blockIndices)
                .keysToCarryForward(List.of("Kategorie"));
        PivotMatrixConverter converter = new PivotMatrixConverter(source);
        String[][] matrix = {
                {"Jahr", "Bevölkerung", "unter 50", "50–60", "60–70", "70–80", "80–90", "90 und mehr", "Pflegebedürftige", "unter 50", "50–60", "60–70", "70–80", "80–90", "90 und mehr", "männlich", "weiblich", "ambulant", "vollstationär", "Pflegegeld", "Sonstige", "Beschäftigte", "ambulant", "stationär"},
                {"2021", "11124642", "6305201", "1726379", "1398493", "918605", "666609", "109355", "540401", "63441", "28584", "50031", "94859", "221796", "81690", "207294", "333107", "93597", "91759", "291159", "63886", "145606", "40052", "105554"},
                /*{"2022", "11.125.983", "6.287.364", "1.690.196", "1.437.922", "932.654", "671.296", "106.551", "547.359", "63.462", "28.145", "51.397", "95.735", "228.788", "79.831", "209.748", "337.611", "95.035", "92.852", "294.783", "64.689", "147.455", "40.648", "106.806"},
                {"2025", "11.170.102", "6.308.847", "1.541.396", "1.557.844", "1.002.293", "640.833", "118.889", "570.352", "64.187", "25.920", "55.922", "103.006", "233.200", "88.116", "218.562", "351.790", "99.898", "98.197", "305.769", "66.487", "155.651", "42.704", "112.947"},
                {"2030", "11.263.812", "6.352.332", "1.383.189", "1.614.177", "1.147.416", "604.053", "162.645", "596.682", "65.300", "22.810", "59.223", "117.919", "211.030", "120.400", "229.152", "367.530", "104.833", "105.533", "318.300", "68.017", "166.202", "44.820", "121.382"},
                {"2035", "11.347.776", "6.358.646", "1.407.983", "1.442.246", "1.316.157", "672.352", "150.392", "624.679", "65.481", "23.041", "54.035", "135.615", "231.610", "114.896", "240.569", "384.110", "110.177", "110.584", "332.278", "71.640", "174.307", "47.080", "127.227"},
                {"2040", "11.421.421", "6.333.915", "1.463.579", "1.311.970", "1.367.814", "779.772", "164.371", "673.560", "65.027", "23.978", "48.188", "145.105", "268.398", "122.864", "258.292", "415.268", "120.590", "119.820", "355.884", "77.266", "189.388", "51.479", "137.909"},
                {"2045", "11.476.228", "6.318.217", "1.481.922", "1.343.159", "1.236.446", "896.998", "199.487", "728.173", "64.560", "24.359", "48.890", "134.218", "307.176", "148.969", "282.344", "445.829", "133.322", "133.501", "379.631", "81.719", "210.623", "57.051", "153.572"},
                {"2050", "11.511.243", "6.326.512", "1.466.207", "1.391.693", "1.142.759", "938.981", "245.090", "774.887", "64.558", "24.100", "50.735", "120.747", "332.260", "182.487", "299.710", "475.177", "144.453", "146.767", "399.335", "84.332", "230.647", "61.814", "168.832"},
                {"2055", "11.538.460", "6.349.482", "1.452.900", "1.406.192", "1.172.029", "859.840", "298.017", "798.141", "64.850", "23.886", "51.519", "122.785", "312.363", "222.737", "309.673", "488.468", "150.219", "156.299", "407.816", "83.806", "244.079", "64.282", "179.797"},
                {"2060", "11.576.523", "6.371.310", "1.446.591", "1.396.059", "1.221.288", "819.544", "321.732", "800.776", "65.165", "23.743", "51.163", "128.253", "289.337", "243.115", "313.015", "487.761", "150.814", "159.534", "407.746", "82.682", "248.054", "64.536", "183.518"}*/
        };

        String[][] expected = {
                {"Jahr", "Kategorie", "Unterkategorie", "Wert"},
                {"2021", "Bevölkerung", "", "11124642"},
                {"2021", "Bevölkerung", "unter 50", "6305201"},
                {"2021", "Bevölkerung", "50–60", "1726379"},
                {"2021", "Bevölkerung", "60–70", "1398493"},
                {"2021", "Bevölkerung", "70–80", "918605"},
                {"2021", "Bevölkerung", "80–90", "666609"},
                {"2021", "Bevölkerung", "90 und mehr", "109355"},
                {"2021", "Pflegebedürftige", "", "540401"},
                {"2021", "Pflegebedürftige", "unter 50", "63441"},
                {"2021", "Pflegebedürftige", "50–60", "28584"},
                {"2021", "Pflegebedürftige", "60–70", "50031"},
                {"2021", "Pflegebedürftige", "70–80", "94859"},
                {"2021", "Pflegebedürftige", "80–90", "221796"},
                {"2021", "Pflegebedürftige", "90 und mehr", "81690"},
                {"2021", "Pflegebedürftige", "männlich", "207294"},
                {"2021", "Pflegebedürftige", "weiblich", "333107"},
                {"2021", "Pflegebedürftige", "ambulant", "93597"},
                {"2021", "Pflegebedürftige", "vollstationär", "91759"},
                {"2021", "Pflegebedürftige", "Pflegegeld", "291159"},
                {"2021", "Pflegebedürftige", "Sonstige", "63886"},
                {"2021", "Beschäftigte", "", "145606"},
                {"2021", "Beschäftigte", "ambulant", "40052"},
                {"2021", "Beschäftigte", "stationär", "105554"},
        };

        String[][] result = converter.handleRequest(matrix);
        Assertions.assertArrayEquals(expected, result);
    }

    @Test
    void testWithMultipleCategoryRequest() {
        Map<String, List<Integer>> map = new LinkedHashMap<>();
        List<Integer> blockIndices = List.of(0);
        map.put("Kategorie", List.of(1, 8, 21));
        map.put("Altersgruppen", List.of(2, 3, 4, 5, 6, 7, 9, 10, 11, 12, 13, 14));
        map.put("Geschlecht", List.of(15, 16));
        map.put("Versorgungsart", List.of(17, 18, 19, 20, 22, 23));


        PivotMatrixStructureDto source = new PivotMatrixStructureDto()
                .pivotField(map)
                .blockIndices(blockIndices)
                .keysToCarryForward(List.of("Kategorie"));
        PivotMatrixConverter converter = new PivotMatrixConverter(source);
        String[][] matrix = {
                {"Jahr", "Bevölkerung", "unter 50", "50–60", "60–70", "70–80", "80–90", "90 und mehr", "Pflegebedürftige", "unter 50", "50–60", "60–70", "70–80", "80–90", "90 und mehr", "männlich", "weiblich", "ambulant", "vollstationär", "Pflegegeld", "Sonstige", "Beschäftigte", "ambulant", "stationär"},
                {"2021", "11124642", "6305201", "1726379", "1398493", "918605", "666609", "109355", "540401", "63441", "28584", "50031", "94859", "221796", "81690", "207294", "333107", "93597", "91759", "291159", "63886", "145606", "40052", "105554"},
        };
        String[][] expected = {
                {"Jahr", "Kategorie", "Altersgruppen", "Geschlecht", "Versorgungsart", "Wert"},
                {"2021", "Bevölkerung", "", "", "", "11124642"},
                {"2021", "Bevölkerung", "unter 50", "", "", "6305201"},
                {"2021", "Bevölkerung", "50–60", "", "", "1726379"},
                {"2021", "Bevölkerung", "60–70", "", "", "1398493"},
                {"2021", "Bevölkerung", "70–80", "", "", "918605"},
                {"2021", "Bevölkerung", "80–90", "", "", "666609"},
                {"2021", "Bevölkerung", "90 und mehr", "", "", "109355"},
                {"2021", "Pflegebedürftige", "", "", "", "540401"},
                {"2021", "Pflegebedürftige", "unter 50", "", "", "63441"},
                {"2021", "Pflegebedürftige", "50–60", "", "", "28584"},
                {"2021", "Pflegebedürftige", "60–70", "", "", "50031"},
                {"2021", "Pflegebedürftige", "70–80", "", "", "94859"},
                {"2021", "Pflegebedürftige", "80–90", "", "", "221796"},
                {"2021", "Pflegebedürftige", "90 und mehr", "", "", "81690"},
                {"2021", "Pflegebedürftige", "", "männlich", "", "207294"},
                {"2021", "Pflegebedürftige", "", "weiblich", "", "333107"},
                {"2021", "Pflegebedürftige", "", "", "ambulant", "93597"},
                {"2021", "Pflegebedürftige", "", "", "vollstationär", "91759"},
                {"2021", "Pflegebedürftige", "", "", "Pflegegeld", "291159"},
                {"2021", "Pflegebedürftige", "", "", "Sonstige", "63886"},
                {"2021", "Beschäftigte", "", "", "", "145606"},
                {"2021", "Beschäftigte", "", "", "ambulant", "40052"},
                {"2021", "Beschäftigte", "", "", "stationär", "105554"}
        };

        String[][] result = converter.handleRequest(matrix);
        Assertions.assertArrayEquals(expected, result);
    }
}
