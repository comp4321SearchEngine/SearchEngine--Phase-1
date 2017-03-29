<html>
<body>
<%@page contentType="text/html" pageEncoding="UTF-8" errorPage="error.jsp"%>

<%@page import="IRUtilities.*" %>
<%@page import="java.util.*" %>
<%

if(request.getParameter("txtname")!=null)
{
	out.println("The results are:<hr/>");
	String string1 = request.getParameter("txtname");

    String[] str1 = string1.split(" ");
	List<String> list = Arrays.asList(str1);
    Vector<String> vector = new Vector<String>(list);

    SearchEngine se = new SearchEngine();
	Vector<Link> result = se.search(vector);
	if(result.size() > 0){
		out.println("<table>");
		
		for(int i = 0; i < result.size(); i++){
			Link temp = result.elementAt(i);
			out.println("<tr><td valign=\"top\">"+temp.getScore()+"</td>");
			out.println("<td>");
			out.println("<a href=\""+temp.getUrl()+"\"> "+temp.getTitle()+"</a><br/>");
			out.println("<a href=\""+temp.getUrl()+"\"> "+temp.getUrl()+"</a><br/>");
			out.println(temp.getLastUpdate()+", "+temp.getPageSize()+"<br/>");
			Vector<Vocab> v1 = temp.getKeywords();
			for(int j = 0; j < v1.size(); j++){
				out.print(v1.elementAt(j).getText()+" "+v1.elementAt(j).getFreq()+"; ");
				if(j==4){
					out.println("<br/>");
					break;
				}
			}
			Vector<String> p = temp.getParentLink();
			for(int j = 0; j< p.size(); j++){
				out.println(p.elementAt(j)+"<br/>");
			}
			Vector<String> c = temp.getChildLink();
			for(int j = 0; j< c.size(); j++){
                out.println(c.elementAt(j)+"<br/>");
            }
			out.println("<br/></td></tr>");
			if(i >= 30)break;
		}
		out.println("</table>");
	}else{
		out.println("No match result");
	}
}
else
{
	out.println("You input nothing");
}

%>
</body>
</html>