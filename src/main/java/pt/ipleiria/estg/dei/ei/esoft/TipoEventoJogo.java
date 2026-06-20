public enum TipoEventoJogo {

    GOLO("Golo"),
    CARTAO_AMARELO("Cartão amarelo"),
    CARTAO_VERMELHO("Cartão vermelho"),
    FALTA("Falta"),
    CANTO("Canto"),
    REMATE("Remate"),
    FORA_DE_JOGO("Fora de jogo");

    private final String descricao;

    TipoEventoJogo(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}