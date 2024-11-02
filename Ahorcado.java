import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class Ahorcado extends JFrame {
    private List<Jugador> jugadores = new ArrayList<>();
    private HashSet<Character> letrasUsadas = new HashSet<>();
    private String fraseActual;
    private char[] fraseOculta;
    private int jugadorIndex;  // Control del índice del jugador actual
    private int puntosParaGanar;

    private JLabel labelFraseOculta, labelInfo;
    private JPanel panelLetras;
    private JButton[] botonesLetras = new JButton[26];

    public Ahorcado() {
        setTitle("Juego del Ahorcado");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        // Label para mostrar la frase oculta
        labelFraseOculta = new JLabel("", SwingConstants.CENTER);
        panel.add(labelFraseOculta);

        // Label para información del jugador actual
        labelInfo = new JLabel("Introduce una letra:", SwingConstants.CENTER);
        panel.add(labelInfo);

        // Panel para las letras del abecedario
        panelLetras = new JPanel();
        panelLetras.setLayout(new GridLayout(4, 7));
        panel.add(panelLetras);

        // Crear botones para cada letra del abecedario
        for (int i = 0; i < 26; i++) {
            char letra = (char) ('a' + i);
            botonesLetras[i] = new JButton(String.valueOf(letra));
            botonesLetras[i].setPreferredSize(new Dimension(50, 50));
            botonesLetras[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    adivinarLetra(letra);
                }
            });
            panelLetras.add(botonesLetras[i]);
        }

        // Agregar el panel principal a la ventana
        add(panel);

        iniciarJuego();
    }

    private void iniciarJuego() {
        puntosParaGanar = Integer.parseInt(JOptionPane.showInputDialog("¿Cuántos puntos se necesitan para ganar?"));
        int numJugadores = Integer.parseInt(JOptionPane.showInputDialog("¿Cuántos jugadores participarán (2-4)?"));

        for (int i = 1; i <= numJugadores; i++) {
            String nombre = JOptionPane.showInputDialog("Ingresa el nombre del jugador " + i + ":");
            jugadores.add(new Jugador(nombre));
        }

        // Establecer el primer jugador como el jugador actual
        jugadorIndex = 0;  // Aseguramos que el primer jugador (jugador 1) sea el que comienza
        iniciarRonda(); // Iniciar la primera ronda
    }

    private void iniciarRonda() {
        letrasUsadas.clear();  // Limpiar las letras usadas de rondas anteriores
        fraseActual = Frases.obtenerFraseAleatoria();  // Obtener una nueva frase aleatoria
        fraseOculta = ocultarFrase(fraseActual);  // Convertir la frase en guiones bajos
        labelFraseOculta.setText(new String(fraseOculta));  // Mostrar la frase oculta

        // Mostrar mensaje para el turno del jugador actual con sus puntos
        actualizarInfoJugador();
    }

    private char[] ocultarFrase(String frase) {
        char[] oculta = new char[frase.length()];
        for (int i = 0; i < frase.length(); i++) {
            oculta[i] = (frase.charAt(i) == ' ') ? ' ' : '_';
        }
        return oculta;
    }

    private void adivinarLetra(char letra) {
        if (letrasUsadas.contains(letra)) {
            labelInfo.setText("Ya usaste esa letra.");
            jugadores.get(jugadorIndex).sumarPuntos(-3);  // Penalización por letra repetida
            pasarTurno();  // Pasar el turno al siguiente jugador
        } else {
            letrasUsadas.add(letra);  // Marcar la letra como usada

            if (fraseActual.toLowerCase().contains(String.valueOf(letra))) {
                revelarLetra(letra);
                labelFraseOculta.setText(new String(fraseOculta));
                int apariciones = contarApariciones(fraseActual, letra);
                jugadores.get(jugadorIndex).sumarPuntos(apariciones * 3);  // 3 puntos por cada aparición

                if (new String(fraseOculta).equalsIgnoreCase(fraseActual)) {
                    labelInfo.setText("¡" + jugadores.get(jugadorIndex).getNombre() + " ha adivinado la frase!");
                    jugadores.get(jugadorIndex).sumarPuntos(5);  // 5 puntos por ganar la ronda
                    if (hayGanador()) {
                        JOptionPane.showMessageDialog(this, jugadores.get(jugadorIndex).getNombre() + " ha ganado el juego!");
                        deshabilitarBotones();  // Deshabilitar los botones al terminar el juego
                    } else {
                        iniciarRonda();  // Iniciar una nueva ronda si no hay ganador
                    }
                } else {
                    // Actualizar información del jugador después de un intento exitoso
                    actualizarInfoJugador();
                }
            } else {
                labelInfo.setText("Letra incorrecta.");
                jugadores.get(jugadorIndex).sumarPuntos(-1);  // Penalización de -1 por letra incorrecta
                pasarTurno();  // Pasar el turno al siguiente jugador
            }
        }
    }

    private void pasarTurno() {
        // Cambiar al siguiente jugador
        jugadorIndex = (jugadorIndex + 1) % jugadores.size();  // Cambiar al siguiente jugador
        actualizarInfoJugador();
    }

    private int contarApariciones(String frase, char letra) {
        int count = 0;
        for (char c : frase.toLowerCase().toCharArray()) {
            if (c == letra) {
                count++;
            }
        }
        return count;
    }

    private void revelarLetra(char letra) {
        for (int i = 0; i < fraseActual.length(); i++) {
            if (fraseActual.toLowerCase().charAt(i) == letra) {
                fraseOculta[i] = fraseActual.charAt(i);
            }
        }
    }

    private boolean hayGanador() {
        for (Jugador jugador : jugadores) {
            if (jugador.getPuntos() >= puntosParaGanar) {
                return true;
            }
        }
        return false;
    }

    private void deshabilitarBotones() {
        for (JButton boton : botonesLetras) {
            boton.setEnabled(false);
        }
    }

    private String obtenerPuntajesJugadores() {
        StringBuilder puntajes = new StringBuilder("Puntajes: ");
        for (Jugador jugador : jugadores) {
            puntajes.append(jugador.getNombre()).append(" (").append(jugador.getPuntos()).append("), ");
        }
        return puntajes.toString().replaceAll(", $", "");  // Eliminar la última coma
    }

    private void actualizarInfoJugador() {
        labelInfo.setText(obtenerPuntajesJugadores() + "\nTurno de " + jugadores.get(jugadorIndex).getNombre() +
                " (Puntos: " + jugadores.get(jugadorIndex).getPuntos() + "): Adivina una letra");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Ahorcado().setVisible(true);
        });
    }
}
