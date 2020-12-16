package by.belstu.fit.projdb1.Connect;

import org.json.JSONException;

import java.io.IOException;

public class Sign {
    Register register;
    public Sign(String user,String password) {
        register=new Register(user,password);
        register.path="https://api.belcraft.ru/v1/auth";
    }

    public void Connect() throws IOException, JSONException {
        register.Connect();
    }

    public String Info() throws JSONException {
        if (!register.response.toString().isEmpty()) return register.authworker.signInfo(register.response.toString());
        return "No Connect";
    }
}
