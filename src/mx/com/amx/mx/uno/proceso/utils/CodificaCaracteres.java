package mx.com.amx.mx.uno.proceso.utils;

public class CodificaCaracteres {
	
	public static String cambiaCaracteres(String texto) {
		texto = texto.replaceAll("á", "&#225;");
		texto = texto.replaceAll("é", "&#233;");
		texto = texto.replaceAll("í", "&#237;");
		texto = texto.replaceAll("ó", "&#243;");
		texto = texto.replaceAll("ú", "&#250;");  
		texto = texto.replaceAll("Á", "&#193;");
		texto = texto.replaceAll("É", "&#201;");
		texto = texto.replaceAll("Í", "&#205;");
		texto = texto.replaceAll("Ó", "&#211;");
		texto = texto.replaceAll("Ú", "&#218;");
		texto = texto.replaceAll("Ñ", "&#209;");
		texto = texto.replaceAll("ñ", "&#241;");        
		texto = texto.replaceAll("ª", "&#170;");          
		texto = texto.replaceAll("ä", "&#228;");
		texto = texto.replaceAll("ë", "&#235;");
		texto = texto.replaceAll("ï", "&#239;");
		texto = texto.replaceAll("ö", "&#246;");
		texto = texto.replaceAll("ü", "&#252;");    
		texto = texto.replaceAll("Ä", "&#196;");
		texto = texto.replaceAll("Ë", "&#203;");
		texto = texto.replaceAll("Ï", "&#207;");
		texto = texto.replaceAll("Ö", "&#214;");
		texto = texto.replaceAll("Ü", "&#220;");
		texto = texto.replaceAll("¿", "&#191;");
		texto = texto.replaceAll("“", "&#8220;");        
		texto = texto.replaceAll("”", "&#8221;");
		texto = texto.replaceAll("‘", "&#8216;");
		texto = texto.replaceAll("’", "&#8217;");
		texto = texto.replaceAll("…", "...");
		texto = texto.replaceAll("¡", "&#161;");
		texto = texto.replaceAll("¿", "&#191;");
		texto = texto.replaceAll("°", "&#176;");
		
		texto = texto.replaceAll("–", "&#8211;");
		texto = texto.replaceAll("—", "&#8212;");
		//texto = texto.replaceAll("\"", "&#34;");
		return texto;
	}
	
	public String cambiaAcentos(String texto) {
		texto = texto.replaceAll("á", "a");
        texto = texto.replaceAll("é", "e");
        texto = texto.replaceAll("í", "i");
        texto = texto.replaceAll("ó", "o");
        texto = texto.replaceAll("ú", "u");  
        texto = texto.replaceAll("Á", "A");
        texto = texto.replaceAll("É", "E");
        texto = texto.replaceAll("Í", "I");
        texto = texto.replaceAll("Ó", "O");
        texto = texto.replaceAll("Ú", "U");
        texto = texto.replaceAll("Ñ", "N");
        texto = texto.replaceAll("ñ", "n");        
        texto = texto.replaceAll("ª", "");          
        texto = texto.replaceAll("ä", "a");
        texto = texto.replaceAll("ë", "e");
        texto = texto.replaceAll("ï", "i");
        texto = texto.replaceAll("ö", "o");
        texto = texto.replaceAll("ü", "u");    
        texto = texto.replaceAll("Ä", "A");
        texto = texto.replaceAll("Ë", "E");
        texto = texto.replaceAll("Ï", "I");
        texto = texto.replaceAll("Ö", "O");
        texto = texto.replaceAll("Ü", "U");
        texto = texto.replaceAll("%", "");        
		return texto;
	}
	
	public static String deleteBloque(String cadena, String simboloInicio, String simboloFin){
		
		int begin = cadena.indexOf(simboloInicio);
		int end = cadena.indexOf(simboloFin) != -1 ? (cadena.indexOf(simboloFin)+1) : -1;
		
		if(begin != -1 && end != -1){
			cadena = cadena.replace(cadena.substring(begin,end), "");
		}
		
		int beginBan = cadena.indexOf(simboloInicio);
		int beginEnd = cadena.indexOf(simboloFin);
		
		if(beginBan != -1 && beginEnd != -1){
			cadena = deleteBloque(cadena, simboloInicio, simboloFin);
		}
		
		return cadena;
	}
	
	public static String deleteHtmlTag(String cadena){
		
		cadena = cadena.replaceAll("&nbsp;", " ");
		
		return cadena;
	}

}
