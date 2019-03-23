import javax.swing.JTextPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

/**
 * ����������ʵ�ֳ������ʶ����Զ����е� JTextPane ������
 * Java 7 ���°汾�� JTextPane ������ʵ���Զ����У���
 * �������ʶ�����Ч������ Java 7 ��ʼ���������ʾͲ����Զ�
 * ���У����� JTextPane ��ʵ�ʿ�ȱ��ʹ�ù��������֡�
 * ����ķ����Ƕ���� bug �ĽϺ��޸���
 *
 * 
 */
public class JIMSendTextPane extends JTextPane {

	// �ڲ���
	// �����ڲ���ȫ������ʵ���Զ�ǿ������

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private class WarpEditorKit extends StyledEditorKit {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ViewFactory defaultFactory = new WarpColumnFactory();

		@Override
		public ViewFactory getViewFactory() {
			return defaultFactory;
		}
	}

	private class WarpColumnFactory implements ViewFactory {

		public View create(Element elem) {
			String kind = elem.getName();
			if (kind != null) {
				if (kind.equals(AbstractDocument.ContentElementName)) {
					return new WarpLabelView(elem);
				} else if (kind.equals(AbstractDocument.ParagraphElementName)) {
					return new ParagraphView(elem);
				} else if (kind.equals(AbstractDocument.SectionElementName)) {
					return new BoxView(elem, View.Y_AXIS);
				} else if (kind.equals(StyleConstants.ComponentElementName)) {
					return new ComponentView(elem);
				} else if (kind.equals(StyleConstants.IconElementName)) {
					return new IconView(elem);
				}
			}

			// default to text display
			return new LabelView(elem);
		}
	}

	private class WarpLabelView extends LabelView {

		public WarpLabelView(Element elem) {
			super(elem);
		}

		@Override
		public float getMinimumSpan(int axis) {
			switch (axis) {
				case View.X_AXIS:
					return 0;
				case View.Y_AXIS:
					return super.getMinimumSpan(axis);
				default:
					throw new IllegalArgumentException("Invalid axis: " + axis);
			}
		}
	}

	// ����

	// ���캯��
	public JIMSendTextPane() {
		super();
		this.setEditorKit(new WarpEditorKit());
	}
}

