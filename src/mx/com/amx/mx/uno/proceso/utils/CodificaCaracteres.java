package mx.com.amx.mx.uno.proceso.utils;

public class CodificaCaracteres {
	
	public static String cambiaCaracteres(String texto) {
		texto = texto.replaceAll("�", "&#225;");
		texto = texto.replaceAll("�", "&#233;");
		texto = texto.replaceAll("�", "&#237;");
		texto = texto.replaceAll("�", "&#243;");
		texto = texto.replaceAll("�", "&#250;");  
		texto = texto.replaceAll("�", "&#193;");
		texto = texto.replaceAll("�", "&#201;");
		texto = texto.replaceAll("�", "&#205;");
		texto = texto.replaceAll("�", "&#211;");
		texto = texto.replaceAll("�", "&#218;");
		texto = texto.replaceAll("�", "&#209;");
		texto = texto.replaceAll("�", "&#241;");        
		texto = texto.replaceAll("�", "&#170;");          
		texto = texto.replaceAll("�", "&#228;");
		texto = texto.replaceAll("�", "&#235;");
		texto = texto.replaceAll("�", "&#239;");
		texto = texto.replaceAll("�", "&#246;");
		texto = texto.replaceAll("�", "&#252;");    
		texto = texto.replaceAll("�", "&#196;");
		texto = texto.replaceAll("�", "&#203;");
		texto = texto.replaceAll("�", "&#207;");
		texto = texto.replaceAll("�", "&#214;");
		texto = texto.replaceAll("�", "&#220;");
		texto = texto.replaceAll("�", "&#191;");
		texto = texto.replaceAll("�", "&#8220;");        
		texto = texto.replaceAll("�", "&#8221;");
		texto = texto.replaceAll("�", "&#8216;");
		texto = texto.replaceAll("�", "&#8217;");
		texto = texto.replaceAll("�", "...");
		texto = texto.replaceAll("�", "&#161;");
		texto = texto.replaceAll("�", "&#191;");
		texto = texto.replaceAll("�", "&#176;");
		
		texto = texto.replaceAll("�", "&#8211;");
		texto = texto.replaceAll("�", "&#8212;");
		//texto = texto.replaceAll("\"", "&#34;");
		return texto;
	}
	
	public String cambiaAcentos(String texto) {
		texto = texto.replaceAll("�", "a");
        texto = texto.replaceAll("�", "e");
        texto = texto.replaceAll("�", "i");
        texto = texto.replaceAll("�", "o");
        texto = texto.replaceAll("�", "u");  
        texto = texto.replaceAll("�", "A");
        texto = texto.replaceAll("�", "E");
        texto = texto.replaceAll("�", "I");
        texto = texto.replaceAll("�", "O");
        texto = texto.replaceAll("�", "U");
        texto = texto.replaceAll("�", "N");
        texto = texto.replaceAll("�", "n");        
        texto = texto.replaceAll("�", "");          
        texto = texto.replaceAll("�", "a");
        texto = texto.replaceAll("�", "e");
        texto = texto.replaceAll("�", "i");
        texto = texto.replaceAll("�", "o");
        texto = texto.replaceAll("�", "u");    
        texto = texto.replaceAll("�", "A");
        texto = texto.replaceAll("�", "E");
        texto = texto.replaceAll("�", "I");
        texto = texto.replaceAll("�", "O");
        texto = texto.replaceAll("�", "U");
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
