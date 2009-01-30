package nested ; 

import org.gjt.sp.jedit.textarea.* ;    
import org.gjt.sp.jedit.buffer.JEditBuffer ;    
import org.gjt.sp.jedit.syntax.Token ;
import org.gjt.sp.jedit.syntax.DefaultTokenHandler ; 
import org.gjt.sp.jedit.Buffer ;      
import java.awt.Graphics2D ;
import java.awt.Color ;
import org.gjt.sp.util.Log ;
import java.awt.Point ;

public class NestedTextAreaExtension extends TextAreaExtension {
	
	private TextArea textArea ; 
	private Color color = Color.decode( "#d7ffd0" ) ;
	
	public NestedTextAreaExtension( TextArea textArea ){
		this.textArea=textArea ;
	}
	
	@Override                       
	public void paintValidLine(Graphics2D gfx, int screenLine,
		int physicalLine, int start, int end, int y){
	
		JEditBuffer buffer = textArea.getBuffer( ) ;
		String mode = buffer.getMode().toString() ;
		Token token = getToken( physicalLine, buffer ) ;
		int height = textArea.getPainter().getFontMetrics().getHeight() ; 
		int width = textArea.getPainter().getFontMetrics().charWidth(0) ;
		Color before = gfx.getColor( ) ;
		
		int offset = 0 ;
		while( token.id != Token.END ){
			int length = token.length ;      
			if( !token.rules.getModeName( ).equals( mode ) ){
				String tokenmode = token.rules.getModeName( ) ;
				
				Point p1; 
				Point p2;
				boolean draw = true ;
				
				if( offset == 0 ){
					p1 = new Point( 0, 0 ) ; 
				} else{
					p1 = textArea.offsetToXY( start + offset ) ;
				}
				
				if( token.next.id == Token.END ){
					p2 = new Point( textArea.getWidth(), 0 )  ;
				} else{
					p2 = textArea.offsetToXY( start + offset + length ) ;
				}
				
				if( p1 != null && p2 != null ){
					gfx.setColor( getColor( mode, tokenmode ) ) ;
					gfx.fillRect( p1.x , y, p2.x - p1.x, height ) ;
					gfx.setColor( before ) ;
				}
				
			}
			offset += length ;
		  token = token.next ;
		}
	}          
	
	private Token getToken( int line, JEditBuffer buffer ){
		DefaultTokenHandler list = new DefaultTokenHandler() ;
    buffer.markTokens(line,list) ;
		return list.getTokens() ;
	}

	public Color getColor( String mainmode, String insidemode ){
		return color ;
	}
	
	
}
