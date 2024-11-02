import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Frases {
    private static final List<String> frases = Arrays.asList(
            "La tecnologia cambia el mundo",
            "El conocimiento es poder",
            "La practica hace al maestro",
            "Aprender es crecer continuamente",
            "El tiempo es el recurso mas valioso",
            "Trabajar en equipo trae grandes resultados",
            "El respeto es la base de toda relacion",
            "La perseverancia vence cualquier obstaculo",
            "La creatividad es la inteligencia divirtiendose"
    );

    public static String obtenerFraseAleatoria() {
        return frases.get(new Random().nextInt(frases.size()));
    }
}
