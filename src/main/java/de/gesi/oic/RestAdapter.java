package de.gesi.oic;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.security.enterprise.identitystore.openid.OpenIdContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.net.URI;

@Path("")
@RequestScoped
public class RestAdapter {

    @Inject
    private OpenIdContext openIdContext;

    @Inject
    private JsonWebToken jwt;

    @Context
    private HttpServletRequest req;

    @GET
    @Path("logout")
    public Response logout() {
        try {
            req.logout();
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return Response.seeOther(URI.create("../api/public")).build();
    }

    @GET
    @Path("secured")
    @RolesAllowed({"__role-name__"})
    public Response secured() {
        return Response.ok("ping Jakarta EE").build();
    }

    @GET
    @Path("public")
    public Response publicResource() {
        StringBuilder sb = new StringBuilder();

        sb.append("<div>");
        {
            sb.append("OpenIdContext subject: ");
            try {
                sb.append(openIdContext.getSubject());
            } catch (NullPointerException ex) {
                sb.append("unauthorized");
            }
        }
        sb.append("</div>");

        sb.append("<pre>");
        {
            sb.append("OpenIdContext claims:\n");
            try {
                sb.append(openIdContext.getClaimsJson().toString());
            } catch (Exception ex) {
                sb.append(ex.getLocalizedMessage());
            }
        }
        sb.append("</pre>");

        sb.append("<pre>");
        {
            sb.append("Some properties:\n");
            try {
                someProperties(openIdContext.getClaimsJson(), sb);
            } catch (Exception ex) {
                sb.append(ex.getLocalizedMessage());
            }
        }
        sb.append("</pre>");

        sb.append("<div>");
        {
            sb.append("JWT subject: ");
            try {
                sb.append(jwt.getSubject());
            } catch (Exception ex) {
                sb.append(ex.getLocalizedMessage());
            }
        }
        sb.append("</div>");


        return Response.ok(sb.toString()).build();
    }

    private void someProperties(JsonObject json, StringBuilder sb) {
        JsonArray jsonArray = json.getJsonArray("some_property");
        if (jsonArray == null) {
            return;
        }
        for (JsonValue jsonValue : jsonArray) {
            JsonObject obj = jsonValue.asJsonObject();
            String clientId = obj.getString("clientId", null);
            String name = obj.getString("name", null);
            String url = obj.getString("url", null);
            sb.append("  ").append(clientId).append(" ").append(name).append(" ").append(url).append("\n");
        }
    }

}
