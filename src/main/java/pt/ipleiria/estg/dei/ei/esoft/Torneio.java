public class Torneio {
    public String name;
    public String startDate;
    public String endDate;
    public String state = "em preparação";
    public int restDays;

    public Torneio(String name, String startDate, String endDate, int restDays) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.restDays = restDays;
    }
}