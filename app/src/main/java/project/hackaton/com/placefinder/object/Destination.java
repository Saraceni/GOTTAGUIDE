package project.hackaton.com.placefinder.object;

/**
 * Created by rafaelgontijo on 11/8/15.
 */
public class Destination {

    private String name;
    private String city;
    private String state;
    private String country;

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getCity()
    {
        return city;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getState()
    {
        return state;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getCountry()
    {
        return country;
    }
}
