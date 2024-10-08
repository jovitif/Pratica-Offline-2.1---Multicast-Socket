package GrupoFechado;

import java.util.Random;

public class Drone {
    private String regiao;
    private int temperatura;
    private int umidade;
    private int pressao;
    private int radiacaoSolar; // Radiação solar em W/m²
    private Random random;

    public Drone(String regiao) {
        this.regiao = regiao;
        this.random = new Random();
    }

    // Gera dados aleatórios de temperatura, umidade, pressão e radiação solar para o drone
    public void gerarDadosAleatorios() {
        if (regiao.equalsIgnoreCase("Norte")) {
            this.temperatura = 25 + random.nextInt(11);   // Temperatura entre 25°C e 35°C
            this.umidade = 60 + random.nextInt(31);       // Umidade entre 60% e 90%
            this.pressao = 1008 + random.nextInt(10);     // Pressão entre 1008 hPa e 1017 hPa
            this.radiacaoSolar = 500 + random.nextInt(501);  // Radiação entre 500 W/m² e 1000 W/m²
        } else if (regiao.equalsIgnoreCase("Sul")) {
            this.temperatura = 10 + random.nextInt(21);   // Temperatura entre 10°C e 30°C
            this.umidade = 60 + random.nextInt(31);       // Umidade entre 60% e 90%
            this.pressao = 1000 + random.nextInt(15);     // Pressão entre 1000 hPa e 1014 hPa
            this.radiacaoSolar = 300 + random.nextInt(401);  // Radiação entre 300 W/m² e 700 W/m²
        }
    }

    // Formata os dados como string
    public String getDados() {
        return String.format("%s: Pressão=%dhPa, Temperatura=%d°C, Umidade=%d%%, Radiação Solar=%dW/m²",
                             regiao, pressao, temperatura, umidade, radiacaoSolar);
    }

    public String getRegiao() {
        return regiao;
    }
}

