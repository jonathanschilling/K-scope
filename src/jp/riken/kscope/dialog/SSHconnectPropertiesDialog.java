package jp.riken.kscope.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import jp.riken.kscope.Message;
import jp.riken.kscope.common.Constant;
import jp.riken.kscope.component.JColorButton;
import jp.riken.kscope.data.ProjectPropertyValue;
import jp.riken.kscope.properties.MemorybandProperties;
import jp.riken.kscope.properties.SSHconnectProperties;

public class SSHconnectPropertiesDialog  extends javax.swing.JDialog implements ActionListener, ListSelectionListener  {
	
	/**
	 * 
	 */
	private SSHconnectProperties settings;
	
	private static final long serialVersionUID = -8218498915763496914L;
	/** キャンセルボタン */
    private JButton btnCancel;
    /** OKボタン */
    private JButton btnOk;
    /** 適用ボタン */
    private JButton btnApply;
    /** プロジェクト設定リスト */
    private JTable tblProperties;
    /** プロジェクト設定リストデータ */
    private DefaultTableModel modelProperties;
    /** 列名 */
    private final String[] COLUMN_HEADERS = {
    		// TODO Set table column headers using Message.getString...
    	"Name",
    	"Value"
    };

    /** ダイアログの戻り値 */
    private int result = Constant.CANCEL_DIALOG;
	
	public SSHconnectPropertiesDialog(Frame frame, SSHconnectProperties settings) {
        super(frame);
        this.settings = settings;
        initGUI();
    }
	
	
	/**
     * GUI初期化を行う。
     */
    private void initGUI() {
    	
    	try {
    		BorderLayout thisLayout = new BorderLayout();
    		thisLayout.setHgap(5);
    		thisLayout.setVgap(5);
    		getContentPane().setLayout(thisLayout);

    		// ボタンパネル
    		{
    			JPanel panelButtons = new JPanel();
    			FlowLayout jPanel1Layout = new FlowLayout();
    			jPanel1Layout.setHgap(10);
    			jPanel1Layout.setVgap(10);
    			panelButtons.setLayout(jPanel1Layout);
    			getContentPane().add(panelButtons, BorderLayout.SOUTH);
    			panelButtons.setPreferredSize(new java.awt.Dimension(390, 46));

    			// メインボタンサイズ
    			java.awt.Dimension buttonSize = new java.awt.Dimension(96, 22);
    			{
    				btnApply = new JButton();
    				btnApply.setText(Message.getString("dialog.common.button.apply")); //適用
    				btnApply.setPreferredSize(buttonSize);
    				btnApply.addActionListener(this);
    				panelButtons.add(btnApply);
    			}
    			{
    				btnOk = new JButton();
    				btnOk.setText(Message.getString("dialog.common.button.ok"));//OK
    				btnOk.setPreferredSize(buttonSize);
    				btnOk.addActionListener(this);
    				panelButtons.add(btnOk);
    			}
    			{
    				btnCancel = new JButton();
    				btnCancel.setText(Message.getString("dialog.common.button.cancel"));//キャンセル
    				btnCancel.setPreferredSize(buttonSize);
    				btnCancel.addActionListener(this);
    				btnCancel.setMargin(new Insets(5, 5, 5, 5));
    				panelButtons.add(btnCancel);
    			}
    		}

    		// コンテンツパネル
    		{
    			JPanel panelContent = new JPanel();
    			BorderLayout panelContentLayout = new BorderLayout();
    			getContentPane().add(panelContent, BorderLayout.CENTER);
    			Border border = new EmptyBorder(7,7,0,7);
    			panelContent.setBorder(border);
    			panelContent.setLayout(panelContentLayout);

    			
    			// Connection to data in SSHconnectProperties class (instance settings)
    			modelProperties = new DefaultTableModel() {
    				/**
					 * 
					 */
					private static final long serialVersionUID = -6996565435968749645L;
					
					public int getColumnCount() { return COLUMN_HEADERS.length; }
    				public int getRowCount() { return settings.count(); }
    				public Object getValueAt(int row, int column) { 
    					if (column > COLUMN_HEADERS.length) {
    						System.err.println("Table has "+COLUMN_HEADERS.length+" columns. You asked for column number"+column);
    						return null;
    					}
    					if (column == 0) return settings.getPropertyName(row);
    					if (column == 1) return settings.getProperty(row);
    					return null;
    				}
    				public boolean isCellEditable(int row, int column) {
    					if (column==1) return true;
    					return false;
    				}
    				public void setValueAt(Object value, int row, int column) {
    					if (column > COLUMN_HEADERS.length) {
    						System.err.println("Table has "+COLUMN_HEADERS.length+" columns. You asked for column number"+column);
    						return;
    					}
    					if (column == 1) settings.setProperty(settings.getPropertyName(row), value.toString());
    					fireTableCellUpdated(row, column);
    			    }
    			};
    			modelProperties.setColumnIdentifiers(COLUMN_HEADERS);
    			tblProperties = new JTable(modelProperties);
    			
    			JScrollPane scrollList = new JScrollPane(tblProperties);
				scrollList.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				scrollList.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
				panelContent.add(scrollList, BorderLayout.CENTER);
    			
    			// プロパティリスト
    			/*{
    				
    				{
    					JScrollPane scrollList = new JScrollPane();
    					scrollList.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    					scrollList.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    					panelContent.add(scrollList, BorderLayout.CENTER);
    					{
    						modelProperties = new DefaultTableModel();
    						modelProperties.setColumnCount(COLUMN_HEADER.length);
    						// ヘッダー列名
    						String[] columns = COLUMN_HEADER;
    						modelProperties.setColumnIdentifiers(columns);
    						tblProperties = new JTable();
    						scrollList.setViewportView(tblProperties);
    						tblProperties.setModel(modelProperties);
    						tblProperties.setSelectionMode(ListSelectionModel.SINGLE_SELECTION );
    						tblProperties.getSelectionModel().addListSelectionListener(this);
    						tblProperties.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    						tblProperties.setDefaultRenderer(Object.class, new DefaultTableCellRenderer());
    						tblProperties.setColumnSelectionAllowed(false);
    						tblProperties.setDefaultEditor(Object.class, null);

    						// 列幅設定
    						// 1列目:PropertyValue:非表示
    						{
    							TableColumn col = tblProperties.getColumnModel().getColumn(0);
    							col.setResizable(false);
    							col.setMinWidth(0);
    							col.setMaxWidth(0);
    						}
    						// 2列目:キー:非表示
    						{
    							TableColumn col = tblProperties.getColumnModel().getColumn(1);
    							col.setResizable(false);
    							col.setMinWidth(0);
    							col.setMaxWidth(0);
    						}
    						// 3列目:タイプ:非表示
    						{
    							TableColumn col = tblProperties.getColumnModel().getColumn(2);
    							col.setResizable(false);
    							col.setMinWidth(0);
    							col.setMaxWidth(0);
    						}    						
    					}
    				}
    			}*/
    		}
    		setTitle("SSHconnect settings"); // TODO set label from Message.getString... 
    		setSize(640, 300);

    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    /**
     * ダイアログを表示する。
     * @return    ダイアログの閉じた時のボタン種別
     */
    public int showDialog() {

        // 親フレーム中央に表示する。
        this.setLocationRelativeTo(this.getOwner());

        // ダイアログ表示
        this.setVisible(true);

        return this.result;
    }
	
	
	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub
		
		// OK
		 // 登録
        if (event.getSource() == this.btnOk) {
            this.result = Constant.OK_DIALOG;

            // 変更内容をソースプロパティに更新する。
            setProperties();

            // 変更イベントを発生
            //this.properties.firePropertyChange();

            // ダイアログを閉じる。
            dispose();
            return;
        }
        // 適用
        if (event.getSource() == this.btnApply) {
            this.result = Constant.OK_DIALOG;

            setProperties();

            // 変更イベントを発生
            //this.properties.firePropertyChange();

            return;
        }
        // 閉じる
        else if (event.getSource() == this.btnCancel) {
            this.result = Constant.CANCEL_DIALOG;
            // ダイアログを閉じる。
            dispose();
            return;
        }
	}
	
	private void setProperties() {
		int rows = this.modelProperties.getRowCount();

        for (int i=0; i<rows; i++) {
            String property_name = (String) this.modelProperties.getValueAt(i, 0);
            String value = (String) this.modelProperties.getValueAt(i, 1);
            settings.setProperty(property_name, value);
        }
        settings.store();
        System.out.println("Settings saved.");
	}


	@Override
	public void valueChanged(ListSelectionEvent event) {
		System.out.println("Value changed");
		// TODO Write code
		/*if (event.getSource() == this.tblProperties.getSelectionModel()) {
            // 選択行を取得する。
            int selectedrow = this.tblProperties.getSelectedRow();
            if (selectedrow < 0) return;

            // "PropertyValue", "キー", "タイプ", "名前", "値", "メッセージ"
            ProjectPropertyValue value = new ProjectPropertyValue(
                        (String)this.modelProperties.getValueAt(selectedrow, 2), //type
                        (String)this.modelProperties.getValueAt(selectedrow, 1), //key
                        (String)this.modelProperties.getValueAt(selectedrow, 4), //value
                        (String)this.modelProperties.getValueAt(selectedrow, 3), //name
                        (String)this.modelProperties.getValueAt(selectedrow, 5)  //message
                    );
            // 選択プロパティ
            this.selectedvalue = value;

            // 設定パネルに表示する
            this.setPropertyPanel(value);
        }*/
	}

}
