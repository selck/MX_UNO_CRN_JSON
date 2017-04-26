package mx.com.amx.mx.uno.proceso.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import mx.com.amx.mx.uno.proceso.dto.ParametrosDTO;

import org.apache.log4j.Logger;

public class ObtenerProperties {
	
	private final Logger LOG = Logger.getLogger(this.getClass().getName());
	
	/** Obtiene los datos del archivo de propiedades externo
	 * @return ParametrosDTO con los datos obtenidos
	 * */
	public ParametrosDTO obtenerPropiedades() {
		ParametrosDTO parametros = new ParametrosDTO();		 
		try {	    		
			Properties propsTmp = new Properties();
		    propsTmp.load(this.getClass().getResourceAsStream( "/general.properties" ));
		    String ambiente = propsTmp.getProperty("ambiente");
			String rutaProperties = propsTmp.getProperty( ambiente  +".ruta.properties");	
			LOG.info("rutaProperties: "+rutaProperties);
			Properties props = new Properties();
			props.load(new FileInputStream(new File(rutaProperties)));			
			
			parametros.setDominio(props.getProperty("dominio") == null? "" : props.getProperty("dominio"));
			parametros.setDominioClarosports(props.getProperty("dominioClarosports") == null? "" : props.getProperty("dominioClarosports"));
			parametros.setLogo(props.getProperty("logo") == null? "" : props.getProperty("logo"));
			parametros.setPathPropertiesDominios(props.getProperty("pathPropertiesDominios") == null? "" : props.getProperty("pathPropertiesDominios"));
			parametros.setRutaCarpeta(props.getProperty("rutaCarpeta") == null? "" : props.getProperty("rutaCarpeta"));
			parametros.setRutaDestino(props.getProperty("rutaDestino") == null? "" : props.getProperty("rutaDestino"));
			parametros.setPathShell(props.getProperty("pathShell") == null? "" : props.getProperty("pathShell"));
			
			//parametros de monitoreo
			parametros.setRutaArchivoMot(props.getProperty("rutaArchivoMot") == null? "" : props.getProperty("rutaArchivoMot"));
			parametros.setRutaEstaticoMot(props.getProperty("rutaEstaticoMot") == null? "" : props.getProperty("rutaEstaticoMot"));
			parametros.setNombreAplicacion(props.getProperty("nombreAplicacion") == null? "" : props.getProperty("nombreAplicacion"));
			parametros.setLine_write(props.getProperty("line_write") == null? "" : props.getProperty("line_write"));
			
			parametros.setPathImagenesAPP(props.getProperty("pathImagenesAPP") == null? "" : props.getProperty("pathImagenesAPP"));
			parametros.setImagenesAPPReplace(props.getProperty("imagenesAPPReplace") == null? "" : props.getProperty("imagenesAPPReplace"));
			parametros.setLimitarSeccion(props.getProperty("limitarSeccion") == null? "" : props.getProperty("limitarSeccion"));
			
			parametros.setVideoURL(props.getProperty("videoURL") == null? "" : props.getProperty("videoURL"));
			parametros.setVideoURLFin(props.getProperty("videoURLFin") == null? "" : props.getProperty("videoURLFin"));
			
			//Twitter
			parametros.setConsumerKey(props.getProperty("consumerKey") == null? "" : props.getProperty("consumerKey"));
			parametros.setConsumerSecret(props.getProperty("consumerSecret") == null? "" : props.getProperty("consumerSecret"));
			parametros.setAccessToken(props.getProperty("accessToken") == null? "" : props.getProperty("accessToken"));
			parametros.setAccessTokenSecret(props.getProperty("accessTokenSecret") == null? "" : props.getProperty("accessTokenSecret"));
			
			//JSONMagazine
			parametros.setIdMagazineHome(props.getProperty("idMagazineHome") == null? "" : props.getProperty("idMagazineHome"));
			parametros.setRutaCarpetaJsonMagazine(props.getProperty("rutaCarpetaJsonMagazine") == null? "" : props.getProperty("rutaCarpetaJsonMagazine"));
			parametros.setNombreJsonMagazine(props.getProperty("nombreJsonMagazine") == null? "" : props.getProperty("nombreJsonMagazine"));
			
			//JSONCarruselClarosports
			parametros.setCarruselCSNumReg(props.getProperty("carruselCSNumReg") == null? 0 : Integer.parseInt(props.getProperty("carruselCSNumReg")));
			parametros.setCarruselCSNombreJson(props.getProperty("carruselCSNombreJson") == null? "" : props.getProperty("carruselCSNombreJson"));
			
			//JSON_AMP
			parametros.setRutaCarpetaJsonAMP(props.getProperty("rutaCarpetaJsonAMP"));
			parametros.setNombreJsonAMP(props.getProperty("nombreJsonAMP"));
			
		} catch (Exception ex) {
			parametros = new ParametrosDTO();
			LOG.error("No se encontro el Archivo de propiedades: ", ex);			
		}
		return parametros;
    }	
}