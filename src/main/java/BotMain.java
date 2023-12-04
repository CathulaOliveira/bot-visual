import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.Robot;

public class BotMain {

    public static void main(String[] args) {

        try {
            // URL desejada
            String url = "https://www.youtube.com";
            String os = System.getProperty("os.name").toLowerCase();
            Runtime rt = Runtime.getRuntime();

            if (os.contains("win")) {
                // Comando para abrir o Google Chrome no Windows
                rt.exec("cmd /c start chrome " + url);
            } else if (os.contains("mac")) {
                // Comando para abrir o Google Chrome no macOS
                rt.exec("open -a /Applications/Google\\ Chrome.app " + url);
            } else if (os.contains("nix") || os.contains("nux")) {
                // Comando para abrir o Google Chrome no Linux
                rt.exec("google-chrome " + url);
            }

            Robot robot = new Robot();
            // Aguarda alguns segundos para abrir o navegador
            Thread.sleep(6000);

            BufferedImage buttonSearch = ImageIO.read(BotMain.class.getResourceAsStream("/pesquisar.JPG"));
            Point pointSearch = findButton(robot, buttonSearch);
            clickOnButton(robot, pointSearch);
            Thread.sleep(6000);

            // Digita algo na barra de pesquisa (simulando o teclado)
            type(robot, "java 17");

            // Simula pressionar a tecla Enter para pesquisar
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);

            // Aguarda um tempo para carregar os resultados da pesquisa
            Thread.sleep(6000);

            // Clicar um pouco a baixo da pesquisa onde deve estar o primeiro video carregado
            pointSearch.y = pointSearch.y + 250;
            clickOnButton(robot, pointSearch);

            Thread.sleep(5000);
            BufferedImage buttonLike = ImageIO.read(BotMain.class.getResourceAsStream("/Capturar.JPG"));
            Point pointLike = findButton(robot, buttonLike);
            clickOnButton(robot, pointLike);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clickOnButton(Robot robot, Point point) throws Exception {
        if (point != null && point.x != -1 && point.y != -1) {
            // Mova o mouse para as coordenadas do botão
            robot.mouseMove(point.x, point.y);

            // Clique no botão
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        } else {
            System.out.println("Botão não encontrado na tela.");
        }
    }

    // Método para simular digitação
    public static void type(Robot robot, String text) {
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            if (Character.isUpperCase(character)) {
                robot.keyPress(KeyEvent.VK_SHIFT);
            }
            robot.keyPress(Character.toUpperCase(character));
            robot.keyRelease(Character.toUpperCase(character));
            if (Character.isUpperCase(character)) {
                robot.keyRelease(KeyEvent.VK_SHIFT);
            }
        }
    }

    public static Point findButton(Robot robot, BufferedImage buttonImage) {
        // Capture a tela atual
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage screenCapture = robot.createScreenCapture(screenRect);

        double threshold = 0.1; // Threshold de similaridade
        int screenWidth = screenCapture.getWidth();
        int screenHeight = screenCapture.getHeight();
        int buttonWidth = buttonImage.getWidth();
        int buttonHeight = buttonImage.getHeight();

        double bestMatch = Double.MAX_VALUE;
        Point bestMatchPoint = null;

        // Converte as imagens para arrays de pixels uma vez para evitar acessos repetidos
        int[] screenPixels = screenCapture.getRGB(0, 0, screenWidth, screenHeight, null, 0, screenWidth);
        int[] buttonPixels = buttonImage.getRGB(0, 0, buttonWidth, buttonHeight, null, 0, buttonWidth);

        for (int y = 0; y < screenHeight - buttonHeight; y++) {
            for (int x = 0; x < screenWidth - buttonWidth; x++) {
                double totalDiff = 0;

                for (int i = 0; i < buttonWidth; i++) {
                    for (int j = 0; j < buttonHeight; j++) {
                        int screenPixel = screenPixels[(y + j) * screenWidth + (x + i)];
                        int buttonPixel = buttonPixels[j * buttonWidth + i];

                        // Extrai os componentes RGB diretamente do valor do pixel
                        int screenRed = (screenPixel >> 16) & 0xFF;
                        int screenGreen = (screenPixel >> 8) & 0xFF;
                        int screenBlue = screenPixel & 0xFF;

                        int buttonRed = (buttonPixel >> 16) & 0xFF;
                        int buttonGreen = (buttonPixel >> 8) & 0xFF;
                        int buttonBlue = buttonPixel & 0xFF;

                        // Calcula a diferença entre as cores RGB
                        double diffRed = Math.abs(screenRed - buttonRed) / 255.0;
                        double diffGreen = Math.abs(screenGreen - buttonGreen) / 255.0;
                        double diffBlue = Math.abs(screenBlue - buttonBlue) / 255.0;

                        // Calcula a diferença total média entre as cores
                        double pixelDiff = (diffRed + diffGreen + diffBlue) / 3.0;
                        totalDiff += pixelDiff;
                    }
                }

                // Calcula a média da diferença total para a região atual
                double averageDiff = totalDiff / (buttonWidth * buttonHeight);

                // Verifica se a média é menor que o melhor resultado encontrado até agora
                if (averageDiff < threshold && averageDiff < bestMatch) {
                    bestMatch = averageDiff;
                    bestMatchPoint = new Point(x, y);
                }
            }
        }

        return bestMatchPoint; // Retorna a melhor posição encontrada
    }


}

