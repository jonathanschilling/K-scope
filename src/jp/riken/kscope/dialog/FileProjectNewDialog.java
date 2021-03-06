/*
 * K-scope
 * Copyright 2012-2013 RIKEN, Japan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.riken.kscope.dialog;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;




//import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import jp.riken.kscope.Message;
import jp.riken.kscope.common.Constant;
import jp.riken.kscope.data.FILE_TYPE;
import jp.riken.kscope.gui.MainFrame;
import jp.riken.kscope.properties.KscopeProperties;
import jp.riken.kscope.properties.ProjectProperties;
import jp.riken.kscope.properties.RemoteBuildProperties;
import jp.riken.kscope.service.AppController;
import jp.riken.kscope.utils.FileUtils;
import jp.riken.kscope.utils.ResourceUtils;
import jp.riken.kscope.utils.StringUtils;
import jp.riken.kscope.utils.SwingUtils;

/**
 * プロジェクトの新規作成ダイアログクラス
 * @author RIKEN
 *
 */
public class FileProjectNewDialog extends javax.swing.JDialog implements ActionListener {

	private static final long serialVersionUID = 6096475381851486225L;
	private static boolean debug = (System.getenv("DEBUG")!= null);	
	private static boolean debug_l2 = false; 
	
/** 中間コード・フォルダ・ファイルリスト */
    private JList<String> listProjectXml;
    /** プロジェクトタイトル */
    private JTextField txtProjectTitle;
    /** プロジェクトフォルダ */
    private JTextField txtProjectFolder;
    /** キャンセルボタン */
    private JButton btnCancel;
    /** プロジェクトフォルダ参照ボタン */
    private JButton btnProjectFolder;
    /** 中間コード・フォルダ参照ボタン */
    private JButton btnXmlFolder;
    /** 中間コード・ファイル参照ボタン */
    private JButton btnXmlFile;
    /** 選択XMLフォルダ・ファイル削除ボタン */
    private JButton btnXmlDelete;
    /** 新構造解析実行チェックボックス **/ //(2012/4/17) added by teraim
    private JCheckBox checkbox_StructureAnalysis;
    /** XMLフォルダ、ファイル追加除外パス名 */
    private List<String> excludeName;
    /** 最終アクセスフォルダ */
    private String lastAccessFolder;
    
    /** フルモード */
    private JRadioButton radioFullMode;
    /** 中間コードの生成を行う */
    private JRadioButton radioGenXML;
    /** 中間コードの生成を行わない */
    private JRadioButton radioNotGenXML;
    /** シンプルモード */
    private JRadioButton radioSimpleMode;
    
    /** Panel holds file_filter and process_files fields */
    private JPanel sshc_settings_panel;
    private JTextField txt_filefilter;
    private JTextArea sshc_text;
    private JTextField txt_preprocess_files;
    private JButton addprerocessfile_button;
    
    /** プロジェクトプロパティ*/
    private ProjectProperties pproperties;
    /** Server プロパティ */
    //private RemoteBuildProperties rb_properties;

    /** Serverを利用する */
    private JCheckBox checkUseRemote;
    private JComboBox<String> settings_list;
    private DefaultComboBoxModel<String> list_model;
    /** 選択XMLフォルダ・ファイル削除ボタン */
    private JButton manage_settings_files;
        
    /** build command and clean command 参照ボタン */
    private JButton btnMakeCmd;
    private JButton btnCleanCmd;
    /** XML パネル説明文 */
    private JTextArea txaXMLPanelDesc;
    /** XML パネル　ラベル */
    private JLabel lblPanelXML;

    private JPanel panelStatusContent = null;
    private final int ARROW_SIZE = 10;
    private JLabel labelStatus[] = new JLabel[5];
    private JPanel panelWizards[] = new JPanel[5];
    private int wizardIndex;
    private int wizardForward;
    private JPanel panelWizardParent;
    private CardLayout cardWizard;
    private final String LABEL_WIZARDS[] = new String[]{"kind", "base", "makefile", "xml", "finalize"};
    /** 戻るボタン */
    private JButton btnBack;
    /** 次へボタン */
    private JButton btnNext;
    /** 構造解析を行う　チェックボックス　表示切り替え */
    private JToggleButton btnAdvancedXml;
    /** プロジェクトを保存する　チェックボックス */
    private JCheckBox cbxSaveProject;
    /** 設定完了画面　中間コード／プロファイラ用ソースラベル */
    private JLabel lblFinalizePanelCode;
    /** エラー出力　テキストエリア */
    private JTextArea txaErrMsg;
    /** ダイアログの戻り値 */
    private int result = Constant.CANCEL_DIALOG;
    /** 参照ボタンサイズ */
    private final java.awt.Dimension REFER_BUTTON_SIZE = new java.awt.Dimension(64, 22);
    /** ビルドコマンドテキストボックス */
    private JTextField txtBuildCommand;
    private JTextField txtCleanCommand;
    
    /**
     * コンストラクタ
     * @param owner		親フレーム
     * @param modal		true=モーダルダイアログを表示する
     * @wbp.parser.constructor
     */
    public FileProjectNewDialog(Frame owner, boolean modal, ProjectProperties pproperties, AppController controller) {
        super(owner, modal);
        if (debug) {
			debug_l2 = (System.getenv("DEBUG").equalsIgnoreCase("high"));
		}
        this.pproperties = pproperties;
        initGUI();
    }

     /**
     * GUI初期化を行う。
     */
    private void initGUI() {
        try {
            BorderLayout thisLayout = new BorderLayout();
            getContentPane().setLayout(thisLayout);

            // 新規作成, キャンセルボタンパネル
            {
                JPanel panelButtons = new JPanel();
                GridBagLayout layoutButton = new GridBagLayout();
                layoutButton.rowWeights = new double[]{0.0};
                layoutButton.rowHeights = new int[]{32};
                layoutButton.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
                layoutButton.columnWidths = new int[]{50, 100, 100, 100, 100, 100, 100, 50};
                panelButtons.setLayout(layoutButton);
                getContentPane().add(panelButtons, BorderLayout.SOUTH);
                panelButtons.setPreferredSize(new java.awt.Dimension(700, 43));

                // 戻る
                java.awt.Dimension buttonSize = new java.awt.Dimension(112, 22);
                {
                    btnBack = new JButton();
                    panelButtons.add(btnBack, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
                    btnBack.setPreferredSize(buttonSize);
                    btnBack.setText(Message.getString("fileprojectnewdialog.button.back")); //< 戻る
                    btnBack.addActionListener(this);
                }
                // 次へ
                {
                    btnNext = new JButton();
                    panelButtons.add(btnNext, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
                    btnNext.setPreferredSize(buttonSize);
                    btnNext.setText(Message.getString("fileprojectnewdialog.button.next")); //次へ >
                    btnNext.addActionListener(this);
                }
                // キャンセルボタン
                {
                    btnCancel = new JButton();
                    panelButtons.add(btnCancel, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
                    btnCancel.setPreferredSize(buttonSize);
                    btnCancel.setText(Message.getString("dialog.common.button.cancel")); //キャンセル
                    btnCancel.addActionListener(this);
                }
            }

            // 種類情報
            this.panelWizards[0] = createWizardKindPanel();
            // 基本情報
            this.panelWizards[1] = createWizardBasePanel();
            // Makefile
            this.panelWizards[2] = createWizardMakefilePanel();
            // 中間コード
            this.panelWizards[3] = createWizardXmlPanel();
            // 完了
            this.panelWizards[4] = createWizardFinalizePanel();
            // ステータスパネル
            JPanel panelStatus = createStatusPanel();

            this.panelWizardParent = new JPanel();
            this.cardWizard = new CardLayout();
            this.panelWizardParent.setLayout(cardWizard);
            this.panelWizardParent.add(LABEL_WIZARDS[0], this.panelWizards[0]);
            this.panelWizardParent.add(LABEL_WIZARDS[1], this.panelWizards[1]);
            this.panelWizardParent.add(LABEL_WIZARDS[2], this.panelWizards[2]);
            this.panelWizardParent.add(LABEL_WIZARDS[3], this.panelWizards[3]);
            JPanel panelCenter = new JPanel();
            BorderLayout layoutCenter = new BorderLayout();
            panelCenter.setLayout(layoutCenter);
            txaErrMsg = new JTextArea();
            txaErrMsg.setLineWrap(true);
            txaErrMsg.setWrapStyleWord(true);
            txaErrMsg.setOpaque(false);
            txaErrMsg.setEditable(false);
            txaErrMsg.setForeground(new Color(255, 0, 0));
            panelCenter.add(txaErrMsg, BorderLayout.SOUTH);
            panelCenter.add(this.panelWizardParent, BorderLayout.CENTER);
            getContentPane().add(panelCenter, BorderLayout.CENTER);
            getContentPane().add(panelStatus, BorderLayout.WEST);
            this.wizardIndex = 0;
            this.wizardForward = 0;
            changeWizardPage(this.wizardIndex);

            // ダイアログサイズ
            this.setSize(760, 360);

            // タイトル
            this.setTitle(Message.getString("mainmenu.file.newproject")); //プロジェクト新規作成

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * New Project Wizard / First screen
     *
     * @return プロジェクト種別選択画面
     */
    private JPanel createWizardKindPanel() {
        JPanel panelContent = new JPanel();
        GridBagLayout jPanel2Layout = new GridBagLayout();
        jPanel2Layout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
        jPanel2Layout.rowHeights = new int[]{32, 32, 32, 32, 16, 16, 16, 16, 7, 7};
        jPanel2Layout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0};
        jPanel2Layout.columnWidths = new int[]{7, 160, 7, 7};
        panelContent.setLayout(jPanel2Layout);
        panelContent.setBorder(new EmptyBorder(7, 7, 0, 7));
        
        // 説明文
        {
            JLabel label = new JLabel(Message.getString("fileprojectnewdialog.statuspanel.kind")); //動作モードの選択 : Select Behavior Mode
            panelContent.add(label, new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

            JTextArea text = new JTextArea(Message.getString("fileprojectnewdialog.kindpanel.desc"));
            text.setLineWrap(true);
            text.setWrapStyleWord(true);
            text.setOpaque(false);
            text.setEditable(false);
            panelContent.add(text, new GridBagConstraints(1, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(7, 7, 10, 0), 0, 0));
        }        
        
        // モード切り替えラジオボタン
        {
            JPanel panelSelect = new JPanel();
            GridBagLayout layoutSelect = new GridBagLayout();
            layoutSelect.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
            layoutSelect.rowHeights = new int[]{16, 16, 16, 16, 16, 16};
            layoutSelect.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0};
            layoutSelect.columnWidths = new int[]{32, 32, 64, 128};
            panelSelect.setLayout(layoutSelect);

            String[] selections = ProjectProperties.getRemoteSettings(); 
        	if (selections.length < 1) {
        		System.out.println("No remote connection settings files found in "+ RemoteBuildProperties.REMOTE_SETTINGS_DIR);
        	} else {
        		System.out.println("Have remote connection settings in "+ RemoteBuildProperties.REMOTE_SETTINGS_DIR);
            	list_model = new DefaultComboBoxModel<String>(selections);
        		settings_list = new JComboBox<String>(list_model);
            	settings_list.setEnabled(false);
            	manage_settings_files = new JButton(Message.getString("fileprojectnewdialog.kindpanel.button.manage_settings_files"));
            	manage_settings_files.setEnabled(false);
                // Remote build の使用切り替え
                checkUseRemote = new JCheckBox(Message.getString("fileprojectnewdialog.kindpanel.checkbox.useServer")) {
					private static final long serialVersionUID = -1195485757658963243L;

					@Override
                    protected void fireStateChanged() {
                        if (pproperties.remoteBuildPossible()) {
                            // Enable settings_list and manage_settings_files only if checkUseRemote is selected
                        	settings_list.setEnabled(this.isSelected());
                        	manage_settings_files.setEnabled(this.isSelected());
                        }
                    }
                };            	
                checkUseRemote.setToolTipText(Message.getString("fileprojectnewdialog.kindpanel.checkbox.useServer.tooltip"));
                checkUseRemote.setEnabled(false);
                // if (debug) System.out.println(isFullProject() && this.rb_properties.remote_settings_found);
                checkUseRemote.setSelected(this.pproperties.useServer());
        	}
            
            //中間コードの生成は行わないラジオボタン（フルモードI）
            radioNotGenXML = new JRadioButton(Message.getString("fileprojectnewdialog.kindpanel.radiobutton.existxml"));
            radioNotGenXML.setToolTipText(Message.getString("fileprojectnewdialog.kindpanel.radiobutton.existxml.tooltip"));

            //ビルドコマンドを用いて中間コード生成ラジオボタン(フルモードII)
            radioGenXML = new JRadioButton(Message.getString("fileprojectnewdialog.kindpanel.radiobutton.genxml")) {

				private static final long serialVersionUID = 8187608139294097396L;

				@Override
                protected void fireStateChanged() {
                    if (this.isSelected()) {
                        pproperties.setHiddenPropertyValue(ProjectProperties.GENERATE_XML, "true");
                    } else {
                        pproperties.setHiddenPropertyValue(ProjectProperties.GENERATE_XML, "false");
                    }
                    if (pproperties.remoteBuildPossible()) {
                        checkUseRemote.setEnabled(this.isSelected());
                        if (!this.isSelected()) {
                        	checkUseRemote.setSelected(false);
                        	settings_list.setEnabled(false);
                        	manage_settings_files.setEnabled(false);
                        }
                    }
                    if (labelStatus[2] != null) {
                        labelStatus[2].setVisible(isGenerateIntermediateCode());
                    }
                    if (labelStatus[3] != null) {
                        labelStatus[3].setText(Message.getString("fileprojectnewdialog.statuspanel.xml")); //中間コードの追加選択
                    }
                    if (panelStatusContent != null) {
                        panelStatusContent.repaint();
                    }
                }
            };
            radioGenXML.setToolTipText(Message.getString("fileprojectnewdialog.kindpanel.radiobutton.genxml.tooltip"));
            
            //フルモードのラジオボタン
            radioFullMode = new JRadioButton(Message.getString("fileprojectnewdialog.kindpanel.radiobutton.fullmode"), true) {

				private static final long serialVersionUID = 346406280524706387L;

				@Override
                protected void fireStateChanged() {
                    if (this.isSelected()) {
                        pproperties.setHiddenPropertyValue(ProjectProperties.FULL_PROJECT, "true");
                    } else {
                        pproperties.setHiddenPropertyValue(ProjectProperties.FULL_PROJECT, "false");
                    }
                    radioGenXML.setEnabled(this.isSelected());
                    radioNotGenXML.setEnabled(this.isSelected());
                    if (labelStatus[3] != null) {
                        String label = null;
                        if (this.isSelected()) {
                            label = Message.getString("fileprojectnewdialog.statuspanel.xml"); //中間コードの選択
                        } else {
                            label = Message.getString("fileprojectnewdialog.statuspanel.fortran"); //フォートランソースの選択
                        }
                        labelStatus[3].setText(label);
                    }
                    if (checkbox_StructureAnalysis != null) {
                        checkbox_StructureAnalysis.setEnabled(this.isSelected());
                        checkbox_StructureAnalysis.setSelected(this.isSelected());
                    }
                    if (remoteBuild(pproperties)) {
                        boolean enabled = this.isSelected() && radioGenXML.isSelected() && radioGenXML.isEnabled();
                        checkUseRemote.setEnabled(enabled);
                        settings_list.setEnabled(enabled); 
                    }
                }
            };
            radioFullMode.setToolTipText(Message.getString("fileprojectnewdialog.kindpanel.radiobutton.fullmode.tooltip")); //K-scopeの全機能を利用可能なモード
                                  
            radioGenXML.setSelected(genXML());
            radioNotGenXML.setSelected(!genXML());

            radioSimpleMode = new JRadioButton(Message.getString("fileprojectnewdialog.kindpanel.radiobutton.simplemode"), false); //簡易モード
            radioSimpleMode.setToolTipText(Message.getString("fileprojectnewdialog.kindpanel.radiobutton.simplemode.tooltip")); //プロファイラ連携機能のみを利用可能なモード

            radioFullMode.addActionListener(this);
            radioSimpleMode.addActionListener(this);
            radioGenXML.addActionListener(this);
            radioNotGenXML.addActionListener(this);

            ButtonGroup groupType = new ButtonGroup();
            groupType.add(radioFullMode);
            groupType.add(radioSimpleMode);
            ButtonGroup groupGenMake = new ButtonGroup();
            groupGenMake.add(radioGenXML);
            groupGenMake.add(radioNotGenXML);
            panelSelect.add(radioFullMode, new GridBagConstraints(0, 0, 4, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            panelSelect.add(radioNotGenXML, new GridBagConstraints(1, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            panelSelect.add(radioGenXML, new GridBagConstraints(1, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            if (this.pproperties != null && this.pproperties.remoteBuildPossible()) {
                panelSelect.add(checkUseRemote, new GridBagConstraints(2, 3, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
                panelSelect.add(settings_list, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
                settings_list.addActionListener(this);
                panelSelect.add(manage_settings_files, new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
                manage_settings_files.addActionListener(this);
            }
            panelSelect.add(radioSimpleMode, new GridBagConstraints(0, 5, 4, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            panelSelect.add(new JLabel(Message.getString("fileprojectnewdialog.kindpanel.label.fortranonly")),
                    new GridBagConstraints(1, 6, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

            panelContent.add(panelSelect, new GridBagConstraints(1, 2, 3, 5, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 7, 0, 0), 0, 0));

        }

        return panelContent;
    }
    
    protected boolean remoteBuild(ProjectProperties pproperties) {
		if (this.checkUseRemote == null) return false;
		if (this.checkUseRemote.isSelected()) {
	        if (pproperties == null) {
	            return false;
	        }
	        return pproperties.useRemoteBuild();
		}
		return false;
    }

	public boolean remoteBuild() {
		return remoteBuild(this.pproperties);
    }
	
    /**
     * True if need to build the project (locally or remotely) to generate
     * intermediate code.
     *
     * @return
     */
    private boolean genXML() {
        return this.pproperties.generateXML();
    }

    /**
     * True if project is full – i.e. includes intermediate code.
     *
     * @return
     */
    private boolean isFullProject() {
        return this.pproperties.isFullProject();
    }

    /**
     * New Project Wizard / Second screen
     *
     * @return プロジェクト基本情報画面
     */
    private JPanel createWizardBasePanel() {

        JPanel panelContent = new JPanel();
        GridBagLayout jPanel2Layout = new GridBagLayout();
        jPanel2Layout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
        jPanel2Layout.rowHeights = new int[]{16, 16, 16, 32, 32, 128};
        jPanel2Layout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0};
        jPanel2Layout.columnWidths = new int[]{7, 160, 7, 7};
        panelContent.setLayout(jPanel2Layout);
        //panelContent.setBorder(new LineBorder(new java.awt.Color(0,0,0), 1, false));
        panelContent.setBorder(new EmptyBorder(7, 7, 0, 7));
        //panelContent.setBorder(BorderFactory.createLineBorder(Color.black));
        // タイトル
        {
            JLabel label = new JLabel(Message.getString("fileprojectnewdialog.statuspanel.basicinfo_long")); //プロジェクト情報の入力
            panelContent.add(label, new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        }
        // 説明文
        {
            JTextArea text = new JTextArea(Message.getString("fileprojectnewdialog.basepanel.desc")); //タイトル：プロジェクトのタイトルを入力...
            text.setLineWrap(true);
            text.setWrapStyleWord(true);
            text.setOpaque(false);
            text.setEditable(false);
            // Init from ProjectProperties        	
            panelContent.add(text, new GridBagConstraints(1, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(7, 0, 10, 0), 0, 0));
        }


        // プロジェクトタイトル
        {
            panelContent.add(new JLabel(Message.getString("fileprojectnewdialog.basepanel.label.title")), //タイトル
                    new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 7, 0, 7), 0, 0));
            txtProjectTitle = new JTextField();
            txtProjectTitle.setText(this.pproperties.getPropertyValue(ProjectProperties.PRJ_TITLE).getValue());
            panelContent.add(txtProjectTitle, new GridBagConstraints(2, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 7), 0, 0));
        }

        // プロジェクトフォルダ
        {
            panelContent.add(new JLabel(Message.getString("fileprojectnewdialog.basepanel.label.projectfolder")), //プロジェクトフォルダ：
                    new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 7, 0, 7), 0, 0));
            JPanel panelFolder = new JPanel();
            GridBagLayout layoutFolder = new GridBagLayout();
            panelContent.add(panelFolder, new GridBagConstraints(2, 3, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 7), 0, 0));
            // panelFolder.setBorder(new LineBorder(new java.awt.Color(0,0,0), 1, false));
            layoutFolder.rowWeights = new double[]{0.1};
            layoutFolder.rowHeights = new int[]{7};
            layoutFolder.columnWeights = new double[]{1.0, 0.0};
            layoutFolder.columnWidths = new int[]{7, 7};
            panelFolder.setLayout(layoutFolder);

            txtProjectFolder = new JTextField();
            panelFolder.add(txtProjectFolder, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

            btnProjectFolder = new JButton();
            panelFolder.add(btnProjectFolder, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            btnProjectFolder.setText(Message.getString("dialog.common.button.refer")); //参照
            btnProjectFolder.setPreferredSize(REFER_BUTTON_SIZE);
            btnProjectFolder.setMargin(new Insets(0, 3, 0, 3));
            btnProjectFolder.addActionListener(this);
            txtProjectFolder.addActionListener(this);
        }

        // Process files & File filter
        if (this.pproperties != null && this.pproperties.remoteBuildPossible()) {
        	
        	
        	
        	// Process files & File filter
        
            sshc_settings_panel = new JPanel();
            GridBagLayout sshc_panel_layout = new GridBagLayout();
            sshc_panel_layout.columnWidths = new int[]{7, 160, 7, 7};
            sshc_panel_layout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0};
            sshc_panel_layout.rowHeights = new int[]{16, 16, 16};
            sshc_settings_panel.setLayout(sshc_panel_layout);
 
            sshc_text = new JTextArea(Message.getString("fileprojectnewdialog.basepanel.filefilter.desc"));
            sshc_text.setLineWrap(true);
            sshc_text.setWrapStyleWord(true);
            sshc_text.setOpaque(false);
            sshc_text.setEditable(false);
 
            JLabel ffl = new JLabel(Message.getString("fileprojectnewdialog.basepanel.filefilter.label"));
            txt_filefilter = new JTextField();
            String ffilter = this.pproperties.getValueByKey(RemoteBuildProperties.FILE_FILTER);
            txt_filefilter.setText(ffilter);
            JLabel procfl = new JLabel(Message.getString("fileprojectnewdialog.basepanel.processfiles.label"));
            txt_preprocess_files = new JTextField();
            String proc_files = this.pproperties.getValueByKey(RemoteBuildProperties.PREPROCESS_FILES);
            txt_preprocess_files.setText(proc_files);
            addprerocessfile_button = new JButton(Message.getString("fileprojectnewdialog.basepanel.processfiles.addbutton"));
            addprerocessfile_button.setEnabled(true);
            addprerocessfile_button.addActionListener(this);
            sshc_settings_panel.add(sshc_text, new GridBagConstraints(0, 0, 4, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 7, 10, 7), 0, 0));
            sshc_settings_panel.add(ffl, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 7, 0, 7), 0, 0));
            sshc_settings_panel.add(txt_filefilter, new GridBagConstraints(2, 1, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            sshc_settings_panel.add(procfl, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 7, 0, 7), 0, 0));
            sshc_settings_panel.add(txt_preprocess_files, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            sshc_settings_panel.add(addprerocessfile_button, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            panelContent.add(sshc_settings_panel, new GridBagConstraints(1, 4, 3, 2, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(7, 0, 0, 7), 0, 0));   
            sshc_settings_panel.setVisible(false);
        }
        return panelContent;
    }

	/**
     * プロジェクトXML追加画面を作成する.
     *
     * @return プロジェクトXML追加画面
     */
    private JPanel createWizardXmlPanel() {

        JPanel panelContent = new JPanel();
        GridBagLayout jPanel2Layout = new GridBagLayout();
        jPanel2Layout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
        jPanel2Layout.rowHeights = new int[]{32, 32, 32, 32, 32, 7};
        jPanel2Layout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0};
        jPanel2Layout.columnWidths = new int[]{7, 160, 7, 7};
        panelContent.setLayout(jPanel2Layout);
        panelContent.setBorder(new EmptyBorder(7, 7, 0, 7));

        // 説明文
        {
            lblPanelXML = new JLabel(Message.getString("fileprojectnewdialog.statuspanel.xml")); //中間コードの選択
            panelContent.add(lblPanelXML, new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        }

        // XMLファイルリスト
        {
            JPanel panelXml = new JPanel();
            GridBagLayout layoutXml = new GridBagLayout();
            panelContent.add(panelXml, new GridBagConstraints(1, 2, 2, 2, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 7, 0, 0), 0, 0));
            layoutXml.rowWeights = new double[]{1.0};
            layoutXml.rowHeights = new int[]{7};
            layoutXml.columnWeights = new double[]{1.0, 0.0};
            layoutXml.columnWidths = new int[]{400, 7};
            panelXml.setLayout(layoutXml);
            {
                listProjectXml = new JList<String>();
                JScrollPane scrollList = new JScrollPane();
                scrollList.getViewport().setView(listProjectXml);
                panelXml.add(scrollList, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

                // 単一選択モード
                listProjectXml.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                // リストモデル
                DefaultListModel<String> model = new DefaultListModel<String>();
                listProjectXml.setModel(model);
                // リストにアイコン表示
                listProjectXml.setCellRenderer(new IconListRenderer());
                // 表示行数=5
                listProjectXml.setVisibleRowCount(5);
            }
            {
                JPanel panelXmlButtons = new JPanel();
                panelXmlButtons.setPreferredSize(new java.awt.Dimension(120, 60));
                BoxLayout layoutXmlButtons = new BoxLayout(panelXmlButtons, javax.swing.BoxLayout.Y_AXIS);
                panelXmlButtons.setLayout(layoutXmlButtons);
                panelXml.add(panelXmlButtons, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

                java.awt.Dimension xmlButtomSize = new java.awt.Dimension(112, 22);
                {
                    btnXmlFolder = new JButton();
                    panelXmlButtons.add(btnXmlFolder);
                    btnXmlFolder.setText(Message.getString("fileprojectnewdialog.xmlpanel.button.addfolder")); //フォルダ追加
                    btnXmlFolder.setPreferredSize(xmlButtomSize);
                    btnXmlFolder.setMinimumSize(xmlButtomSize);
                    btnXmlFolder.setMaximumSize(xmlButtomSize);
                    btnXmlFolder.addActionListener(this);
                }
                {
                    btnXmlFile = new JButton();
                    panelXmlButtons.add(btnXmlFile);
                    btnXmlFile.setText(Message.getString("fileprojectnewdialog.xmlpanel.button.addfile")); //ファイル追加
                    btnXmlFile.setPreferredSize(xmlButtomSize);
                    btnXmlFile.setMinimumSize(xmlButtomSize);
                    btnXmlFile.setMaximumSize(xmlButtomSize);
                    btnXmlFile.addActionListener(this);
                }
                {
                    btnXmlDelete = new JButton();
                    panelXmlButtons.add(btnXmlDelete);
                    btnXmlDelete.setText(Message.getString("dialog.common.button.delete")); //削除
                    btnXmlDelete.setPreferredSize(xmlButtomSize);
                    btnXmlDelete.setMinimumSize(xmlButtomSize);
                    btnXmlDelete.setMaximumSize(xmlButtomSize);
                    btnXmlDelete.addActionListener(this);
                }
            }
        }
        // 構造解析を行う
        {
            JPanel panelAdvanced = new JPanel();
            GridBagLayout layout = new GridBagLayout();
            layout.rowWeights = new double[]{1.0, 1.0};
            layout.rowHeights = new int[]{7, 7};
            layout.columnWeights = new double[]{0.0, 0.0};
            layout.columnWidths = new int[]{7, 7};
            panelAdvanced.setLayout(layout);
            btnAdvancedXml = new JToggleButton("Advanced >>", false) {

				private static final long serialVersionUID = 8408253832919667943L;

				@Override
                protected void fireStateChanged() {
                    checkbox_StructureAnalysis.setVisible(this.isSelected());
                    cbxSaveProject.setVisible(this.isSelected());
                }
            };
            panelAdvanced.add(btnAdvancedXml,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            checkbox_StructureAnalysis = new JCheckBox(Message.getString("fileprojectnewdialog.xmlpanel.checkbox.build"), true); //構造解析を行う
            checkbox_StructureAnalysis.setVisible(btnAdvancedXml.isSelected());
            panelAdvanced.add(checkbox_StructureAnalysis,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 14, 0, 0), 0, 0));
            cbxSaveProject = new JCheckBox(Message.getString("fileprojectnewdialog.xmlpanel.checkbox.save"), false); //作成後にプロジェクトを保存する
            cbxSaveProject.setVisible(btnAdvancedXml.isSelected());
            panelAdvanced.add(cbxSaveProject,
                    new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 14, 0, 0), 0, 0));
            panelContent.add(panelAdvanced, new GridBagConstraints(1, 4, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(7, 7, 0, 0), 0, 0));
        }

        // 説明文
        {
            txaXMLPanelDesc = new JTextArea(Message.getString("fileprojectnewdialog.xmlpanel.desc")); //中間コードのフォルダまたはファイルを選択してください。
            txaXMLPanelDesc.setLineWrap(true);
            txaXMLPanelDesc.setWrapStyleWord(true);
            txaXMLPanelDesc.setOpaque(false);
            txaXMLPanelDesc.setEditable(false);
            panelContent.add(txaXMLPanelDesc, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(7, 7, 10, 0), 0, 0));
        }
        
        return panelContent;
    }

    /**
     * プロジェクトMakefile画面を作成する.
     *
     * @return プロジェクトMakefile画面
     */
    private JPanel createWizardMakefilePanel() {

        JPanel panelContent = new JPanel();
        GridBagLayout jPanel2Layout = new GridBagLayout();
        jPanel2Layout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
        jPanel2Layout.rowHeights = new int[]{32, 32, 32, 32, 32, 7, 10};
        jPanel2Layout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0};
        jPanel2Layout.columnWidths = new int[]{7, 120, 7, 7};
        panelContent.setLayout(jPanel2Layout);
        panelContent.setBorder(new EmptyBorder(7, 7, 0, 7));

        // 説明文 1
        {
        	JLabel label = new JLabel(Message.getString("fileprojectnewdialog.statuspanel.make_long")); //中間コードのビルド連携の設定
        	panelContent.add(label, new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        }

        // 説明文 2
        {
        	JTextArea text = new JTextArea(Message.getString("fileprojectnewdialog.makefilepanel.desc")); //実行するビルドコマンドを選択してください。
        	text.setLineWrap(true);
        	text.setWrapStyleWord(true);
        	text.setOpaque(false);
        	text.setEditable(false);
        	panelContent.add(text, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(7, 0, 10, 0), 0, 0));
        }

        // Build command
        {
            panelContent.add(new JLabel(Message.getString("fileprojectnewdialog.makefilepanel.label.makecommand")), //ビルドコマンド
                    new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 7, 0, 7), 0, 0));
            txtBuildCommand = new JTextField();
            btnMakeCmd = new JButton();
            btnMakeCmd.setText(Message.getString("dialog.common.button.refer")); //参照
            btnMakeCmd.setPreferredSize(REFER_BUTTON_SIZE);
            btnMakeCmd.setMinimumSize(REFER_BUTTON_SIZE);
            btnMakeCmd.setMaximumSize(REFER_BUTTON_SIZE);
            btnMakeCmd.setMargin(new Insets(0, 3, 0, 3));
            btnMakeCmd.addActionListener(this);
            panelContent.add(txtBuildCommand,
            		new GridBagConstraints(2, 2, 2, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            panelContent.add(btnMakeCmd,
            		new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        }
        
        // Clean command
        {
        	panelContent.add(new JLabel(Message.getString("fileprojectnewdialog.makefilepanel.clean-command.dsc")), // Clean command description
                    new GridBagConstraints(1, 3, 4, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            panelContent.add(new JLabel(Message.getString("fileprojectnewdialog.makefilepanel.clean-command.name")), // Clean command
                    new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 7, 0, 7), 0, 0));
            txtCleanCommand = new JTextField();
            btnCleanCmd = new JButton();
            btnCleanCmd.setText(Message.getString("dialog.common.button.refer")); //参照
            btnCleanCmd.setPreferredSize(REFER_BUTTON_SIZE);
            btnCleanCmd.setMinimumSize(REFER_BUTTON_SIZE);
            btnCleanCmd.setMaximumSize(REFER_BUTTON_SIZE);
            btnCleanCmd.setMargin(new Insets(0, 3, 0, 3));
            btnCleanCmd.addActionListener(this);
            panelContent.add(txtCleanCommand,
            		new GridBagConstraints(2, 4, 2, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            panelContent.add(btnCleanCmd,
            		new GridBagConstraints(4, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        }

        

        return panelContent;
    }

    /**
     * プロジェクト作成最終確認画面を作成する.
     *
     * @return プロジェクト作成最終確認画面
     */
    private JPanel createWizardFinalizePanel() {

        int row = 0;
        JPanel panelContent = new JPanel();
        GridBagLayout jPanel2Layout = new GridBagLayout();
        jPanel2Layout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
        jPanel2Layout.rowHeights = new int[]{32, 32, 32, 32, 32, 32, 32, 8, 7, 7, 10};
        jPanel2Layout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0};
        jPanel2Layout.columnWidths = new int[]{7, 160, 7, 7};
        panelContent.setLayout(jPanel2Layout);
        panelContent.setBorder(new EmptyBorder(7, 7, 0, 7));

        // 説明文
        row = 0;
        {
            JLabel label = new JLabel(Message.getString("fileprojectnewdialog.statuspanel.confirm_long")); //入力情報の確認            
            panelContent.add(label, new GridBagConstraints(0, row, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        }

        // モード
        row++;
        {
            panelContent.add(new JLabel(Message.getString("fileprojectnewdialog.finalizepanel.label.mode")), // 動作モード
                    new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 7), 0, 0));

            String str = null;
            if (isFullProject()) {
                str = radioFullMode.getText();
            } else {
                if (isSelectedSimpleMode()) {
                    str = radioSimpleMode.getText();
                } else {
                    str = "error: unknown mode";
                }
            }
            
            JLabel label = new JLabel(str);
            panelContent.add(label, new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }

        // プロジェクトタイトル
        row++;
        {
            panelContent.add(new JLabel(Message.getString("fileprojectnewdialog.basepanel.label.title")), //タイトル
                    new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 7), 0, 0));
            JLabel label = new JLabel(txtProjectTitle.getText());
            panelContent.add(label, new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        // プロジェクトフォルダ
        row++;
        {
            panelContent.add(new JLabel(Message.getString("fileprojectnewdialog.basepanel.label.projectfolder")), //プロジェクトフォルダ
                    new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 7), 0, 0));
            JLabel label = new JLabel(txtProjectFolder.getText());
            panelContent.add(label, new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }

        // Makeコマンド
        String makecmd = null;
        if (isGenerateIntermediateCode()) {
            row++;
            {
                panelContent.add(new JLabel(Message.getString("fileprojectnewdialog.makefilepanel.label.makecommand")), //Makeコマンド
                        new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 7), 0, 0));
                makecmd = this.txtBuildCommand.getText();
                JLabel label = new JLabel(makecmd);
                panelContent.add(label, new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            }
        }
        
     // Clean command
        String cleancmd = null;
        if (isGenerateIntermediateCode()) {
            row++;
            {
                panelContent.add(new JLabel(Message.getString("fileprojectnewdialog.makefilepanel.clean-command.name")), // Clean command
                        new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 7), 0, 0));
                cleancmd = this.txtCleanCommand.getText();
                JLabel label = new JLabel(cleancmd);
                panelContent.add(label, new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            }
        }
        
        
        // 中間コード　or フォートランソース
        row++;
        {
            String labelString = Message.getString("fileprojectnewdialog.finalizepanel.label.xml"); //中間コード
            if (isSelectedSimpleMode()) {
                labelString = Message.getString("fileprojectnewdialog.finalizepanel.label.fortran"); //フォートランソース
            }
            lblFinalizePanelCode = new JLabel(labelString);
            panelContent.add(lblFinalizePanelCode,
                    new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 7), 0, 0));
            String xml = null;
            DefaultListModel<String> model = (DefaultListModel<String>) this.listProjectXml.getModel();
            for (int i = 0; i < model.getSize(); i++) {
                String pathname = model.getElementAt(i);
                if (xml == null) {
                    xml = new String();
                } else {
                    xml += ",";
                }
                xml += pathname;
            }
            JLabel label = new JLabel(xml);
            panelContent.add(label, new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }

        // 構造解析を行う
        if ((this.radioFullMode.isSelected() && !this.checkbox_StructureAnalysis.isSelected())) {
            row++;
            {
                panelContent.add(new JLabel(Message.getString("fileprojectnewdialog.finalizepanel.label.build")), //構造解析
                        new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 7), 0, 0));
                JLabel label = new JLabel(Message.getString("fileprojectnewdialog.finalizepanel.savecheck.donot")); //行わない
                panelContent.add(label, new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            }
        }

        // 保存を行う
        if (isSave()) {
            row++;
            {
                panelContent.add(new JLabel(Message.getString("fileprojectnewdialog.finalizepanel.label.save")), //作成後に保存
                        new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 7), 0, 0));
                JLabel label = new JLabel(Message.getString("fileprojectnewdialog.finalizepanel.savecheck.do")); //行う
                panelContent.add(label, new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            }
        }

        return panelContent;
    }

    /**
     * プロジェクト作成進捗画面を作成する.
     *
     * @return プロジェクト作成進捗画面
     */
    private JPanel createStatusPanel() {
        final int ROW_HEIGHT = 16;
        final int LABEL_WIDTH = 72;

        this.panelStatusContent = new JPanel() {
			private static final long serialVersionUID = -1481838304987157059L;

			@Override
            public void paintComponent(Graphics g) {

                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.BLACK);

                BasicStroke wideStroke = new BasicStroke(2.0f);
                g2.setStroke(wideStroke);

                // 矢印
                int x0, y0, x1, y1;
                int xp[] = new int[3], yp[] = new int[3];
                // 0->1
                {
                    Rectangle rect0 = labelStatus[0].getBounds();
                    Rectangle rect1 = labelStatus[1].getBounds();
                    x0 = rect0.x + rect0.width / 2;
                    y0 = rect0.y + rect0.height;
                    x1 = rect1.x + rect1.width / 2;
                    y1 = rect1.y;

                    g2.drawLine(x0, y0, x1, y1);

                    xp[0] = x1;
                    yp[0] = y1;
                    xp[1] = x1 - ARROW_SIZE / 2;
                    yp[1] = y1 - ARROW_SIZE;
                    xp[2] = x1 + ARROW_SIZE / 2;
                    yp[2] = y1 - ARROW_SIZE;
                    g2.fillPolygon(xp, yp, 3);
                }
                // 1->2
                {
                    Rectangle rect0 = labelStatus[1].getBounds();
                    Rectangle rect1 = labelStatus[2].getBounds();
                    x0 = rect0.x + rect0.width / 2;
                    y0 = rect0.y + rect0.height;
                    x1 = rect1.x + rect1.width / 2;
                    y1 = rect1.y;

                    if (isGenerateIntermediateCode()) {
                        g2.drawLine(x0, y0, x1, y1);

                        xp[0] = x1;
                        yp[0] = y1;
                        xp[1] = x1 - ARROW_SIZE / 2;
                        yp[1] = y1 - ARROW_SIZE;
                        xp[2] = x1 + ARROW_SIZE / 2;
                        yp[2] = y1 - ARROW_SIZE;
                        g2.fillPolygon(xp, yp, 3);
                    }
                }
                // 2->3
                {
                    Rectangle rect0 = labelStatus[2].getBounds();
                    Rectangle rect1 = labelStatus[3].getBounds();
                    x0 = rect0.x + rect0.width / 2;
                    y0 = rect0.y + rect0.height;
                    x1 = rect1.x + rect1.width / 2;
                    y1 = rect1.y;

                    if (isGenerateIntermediateCode()) {
                        g2.drawLine(x0, y0, x1, y1);

                        xp[0] = x1;
                        yp[0] = y1;
                        xp[1] = x1 - ARROW_SIZE / 2;
                        yp[1] = y1 - ARROW_SIZE;
                        xp[2] = x1 + ARROW_SIZE / 2;
                        yp[2] = y1 - ARROW_SIZE;
                        g2.fillPolygon(xp, yp, 3);
                    }
                }
                // 1->3
                {
                    Rectangle rect0 = labelStatus[1].getBounds();
                    Rectangle rect1 = labelStatus[3].getBounds();
                    x0 = rect0.x + rect0.width / 2;
                    y0 = rect0.y + rect0.height;
                    x1 = rect1.x + rect1.width / 2;
                    y1 = rect1.y;

                    if (!isGenerateIntermediateCode()) {
                        g2.drawLine(x0, y0, x1, y1);

                        xp[0] = x1;
                        yp[0] = y1;
                        xp[1] = x1 - ARROW_SIZE / 2;
                        yp[1] = y1 - ARROW_SIZE;
                        xp[2] = x1 + ARROW_SIZE / 2;
                        yp[2] = y1 - ARROW_SIZE;
                        g2.fillPolygon(xp, yp, 3);
                    }

                }
                // 3->4
                {
                    Rectangle rect0 = labelStatus[3].getBounds();
                    Rectangle rect1 = labelStatus[4].getBounds();
                    x0 = rect0.x + rect0.width / 2;
                    y0 = rect0.y + rect0.height;
                    x1 = rect1.x + rect1.width / 2;
                    y1 = rect1.y;

                    g2.drawLine(x0, y0, x1, y1);

                    xp[0] = x1;
                    yp[0] = y1;
                    xp[1] = x1 - ARROW_SIZE / 2;
                    yp[1] = y1 - ARROW_SIZE;
                    xp[2] = x1 + ARROW_SIZE / 2;
                    yp[2] = y1 - ARROW_SIZE;
                    g2.fillPolygon(xp, yp, 3);
                }

            }
        };

        GridBagLayout jPanel2Layout = new GridBagLayout();
        jPanel2Layout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        jPanel2Layout.rowHeights = new int[]{32, ROW_HEIGHT, 32, ROW_HEIGHT, 32, ROW_HEIGHT, 32, ROW_HEIGHT, 32, 7};
        jPanel2Layout.columnWeights = new double[]{0.0, 0.0};
        jPanel2Layout.columnWidths = new int[]{7, 7};
        panelStatusContent.setLayout(jPanel2Layout);
        EmptyBorder border = new EmptyBorder(7, 7, 7, 7);
        panelStatusContent.setBorder(border);

        Font font = new Font("Meiryo UI", Font.PLAIN, 11);
        java.awt.Dimension sizex2 = new java.awt.Dimension(LABEL_WIDTH * 2, 22);
        labelStatus[0] = new JLabel(Message.getString("fileprojectnewdialog.statuspanel.kind")); //動作モードの選択
        labelStatus[0].setPreferredSize(sizex2);
        labelStatus[0].setBorder(new LineBorder(Color.BLACK));
        labelStatus[0].setHorizontalAlignment(JLabel.CENTER);
        labelStatus[0].setFont(font);
        panelStatusContent.add(labelStatus[0], new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        labelStatus[1] = new JLabel(Message.getString("fileprojectnewdialog.statuspanel.basicinfo")); //プロジェクト情報の入力
        labelStatus[1].setPreferredSize(sizex2);
        labelStatus[1].setBorder(new LineBorder(Color.BLACK));
        labelStatus[1].setHorizontalAlignment(JLabel.CENTER);
        labelStatus[1].setFont(font);
        panelStatusContent.add(labelStatus[1], new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        labelStatus[2] = new JLabel(Message.getString("fileprojectnewdialog.statuspanel.make")); //make連携情報の入力
        labelStatus[2].setPreferredSize(sizex2);
        labelStatus[2].setBorder(new LineBorder(Color.BLACK));
        labelStatus[2].setHorizontalAlignment(JLabel.CENTER);
        labelStatus[2].setFont(font);
        panelStatusContent.add(labelStatus[2], new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        labelStatus[2].setVisible(isGenerateIntermediateCode());

        labelStatus[3] = new JLabel(Message.getString("fileprojectnewdialog.statuspanel.xml")); //中間コードの選択
        labelStatus[3].setPreferredSize(sizex2);
        labelStatus[3].setBorder(new LineBorder(Color.BLACK));
        labelStatus[3].setHorizontalAlignment(JLabel.CENTER);
        labelStatus[3].setFont(font);
        panelStatusContent.add(labelStatus[3], new GridBagConstraints(0, 6, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        labelStatus[4] = new JLabel(Message.getString("fileprojectnewdialog.statuspanel.confirm")); //入力情報の確認
        labelStatus[4].setPreferredSize(sizex2);
        labelStatus[4].setBorder(new LineBorder(Color.BLACK));
        labelStatus[4].setHorizontalAlignment(JLabel.CENTER);
        labelStatus[4].setFont(font);
        panelStatusContent.add(labelStatus[4], new GridBagConstraints(0, 8, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        JPanel outsideContent = new JPanel();
        EmptyBorder outsideBorder = new EmptyBorder(7, 7, 7, 7);
        // BevelBorder insideBorder = new BevelBorder(BevelBorder.LOWERED);
        EtchedBorder insideBorder = new EtchedBorder(EtchedBorder.LOWERED);
        CompoundBorder compborder = new CompoundBorder(outsideBorder, insideBorder);
        outsideContent.setBorder(compborder);
        outsideContent.add(panelStatusContent, BorderLayout.CENTER);

        return outsideContent;
    }

    /**
     * プロジェクト新規作成画面を切り替える.
     *
     * @param index	切替画面Index
     */
    private void changeWizardPage(int index) {
        
        //最終ページ
        if (index == panelWizards.length - 1) {
            this.panelWizardParent.remove(this.panelWizards[index]);
            this.cardWizard.removeLayoutComponent(this.panelWizards[index]);
            this.panelWizards[index] = createWizardFinalizePanel();
            this.panelWizardParent.add(LABEL_WIZARDS[index], this.panelWizards[index]);
        }
        this.cardWizard.show(this.panelWizardParent, LABEL_WIZARDS[index]);
        for (int i = 0; i < this.labelStatus.length; i++) {
            this.labelStatus[i].setBackground(Color.WHITE);
            this.labelStatus[i].setOpaque(true);
        }
        this.labelStatus[index].setBackground(new Color(255, 160, 122));

        this.btnBack.setEnabled(true);
        this.btnNext.setEnabled(true);
        this.btnNext.setText(Message.getString("fileprojectnewdialog.button.next")); //次へ >
        this.btnCancel.setEnabled(true);
        
        //
        if (index == 0) {
            this.btnBack.setEnabled(false);
        } else if (index == 1) { // open BasePanel
            // file_filter and process_files fields on sshc_settings_panel
            if (this.pproperties.remoteBuildPossible() && checkUseRemote.isSelected()) {
            	
            	String settings_file = (String)this.settings_list.getSelectedItem();
            	String remote_service = ProjectProperties.getRemoteService(settings_file);
            	System.out.println("Remote service is "+remote_service);
            	if (remote_service.equalsIgnoreCase(RemoteBuildProperties.REMOTE_SERVICE_SSHCONNECT)) {
                	sshc_settings_panel.setVisible(true);
                }
                else {
                	sshc_settings_panel.setVisible(false);
                }
            }
            else if (this.pproperties.useRemoteBuild()) {
            	// If remoteBuild returns false, sshc_settings_panel is null
            	sshc_settings_panel.setVisible(false);
            }
            if (this.txtProjectFolder.getText().length() < 1) {
                this.btnNext.setEnabled(false);  // Disable until project folder selected    		
            }
        } else if (index == 3) {
            //フルモードは中間コード、簡易モードはFortranコードを追加するためメッセージを切り替える。
            String title = null;
            String desc = null;
            //フルモード
            if (isFullProject()) {
                title = Message.getString("fileprojectnewdialog.statuspanel.xml_long");
                radioFullMode.setSelected(true);
                //中間コードを生成するモード（フルモードII）
                if (isGenerateIntermediateCode()) {
                    // makefileのparentを設定
                    String sf = this.txtProjectFolder.getText();
                    if (!StringUtils.isNullOrEmpty(sf)) {
                        File f = new File(sf);
                        addProjectList(new File[]{f});
                    }
                    desc = Message.getString("fileprojectnewdialog.xmlpanel.additionalfile.desc");
                }
                //既存の中間コードを読み込むモード(フルモードI)
                else {
                    // 中間コードが空の場合は自動的にプロジェクトフォルダを設定
                    if (this.listProjectXml.getModel().getSize() < 1) {
                        File f = new File(this.txtProjectFolder.getText());
                        addProjectList(new File[]{f});
                    }
                    desc = Message.getString("fileprojectnewdialog.xmlpanel.desc");
                }
            }
            //簡易モード
            else {
                title = Message.getString("fileprojectnewdialog.statuspanel.fortran");
                desc = Message.getString("fileprojectnewdialog.xmlpanel.fortran.desc");
            }
            lblPanelXML.setText(title);
            txaXMLPanelDesc.setText(desc);
            
            //(2013/10/16) added by teraim
            // Remote buildを使う場合は、既存中間コードの追加をdisableにする。
            String rs_file = remoteSettingsFile();
            if (rs_file != null && rs_file.length() > 0) {
                listProjectXml.setEnabled(false);
                btnXmlFolder.setEnabled(false);
                btnXmlFile.setEnabled(false);
                btnXmlDelete.setEnabled(false);
            }
            else{
                listProjectXml.setEnabled(true);
                btnXmlFolder.setEnabled(true);
                btnXmlFile.setEnabled(true);
                btnXmlDelete.setEnabled(true);            
            }            
            
        } else if (index == panelWizards.length - 1) {
            this.btnNext.setText(Message.getString("fileprojectnewdialog.button.new")); //新規作成
            String labelString = Message.getString("fileprojectnewdialog.finalizepanel.label.xml"); //中間コード
            if (isSelectedSimpleMode()) {
                labelString = Message.getString("fileprojectnewdialog.finalizepanel.label.fortran"); //Fortranソース
            }
            lblFinalizePanelCode.setText(labelString);
        }
    }

    /**
     * 設定パラメータチェック
     *
     * @param	index	ウィザードパネル番号
     * @return	true=成功, false=失敗
     */
    private boolean checkParams(int index) {
        String msg = "";
        boolean ret = true;
        if (index == 1) {
            // タイトルは無くてもいいからチェックしない
            // プロジェクトフォルダ
            String s = getProjectFolder();
            if (StringUtils.isNullOrEmpty(s)) {
                msg = Message.getString("fileprojectnewdialog.errordialog.message.projectfolderempty"); //"プロジェクトフォルダは必須項目です。"
                ret = false;
            } else {
                File f = new File(s);
                if (!f.exists()) {
                    msg = Message.getString("fileprojectnewdialog.errordialog.message.projectfoldermissing"); //指定されたプロジェクトフォルダは存在しません。
                    msg += " [" + s + "]";
                    ret = false;
                } else if (!f.isDirectory()) {
                    msg = Message.getString("fileprojectnewdialog.errordialog.message.projectfolderisnotdirectory"); //指定されたプロジェクトフォルダはディレクトリではありません。
                    ret = false;
                }
            }
        } else if (index == 2) {
            // makeコマンド
            String m = getBuildCommand();
            if (StringUtils.isNullOrEmpty(m)) {
                msg = Message.getString("fileprojectnewdialog.errordialog.message.makecommandempty"); //Makeコマンドが設定されていません。
                ret = false;
            }
            // makefileパス
    		/*String p = getMakefilePath();
             if (StringUtils.isNullOrEmpty(p)) {
             if (!StringUtils.isNullOrEmpty(msg)) {
             msg += "\n";
             }
             msg += Message.getString("fileprojectnewdialog.errordialog.message.makefileempty"); //Makefileパスが設定されていません。
             ret = false;
             }*/
        } else if (index == 3) {
            List<File> list = getProjectXmlList();
            if (list != null && list.size() > 0) {
                for (File file : list) {
                    if (!file.exists()) {
                        if (!StringUtils.isNullOrEmpty(msg)) {
                            msg += "\n";
                        }
                        msg += Message.getString("fileprojectnewdialog.errordialog.filenotexist.message", file.getName()); //ファイル[%s]が存在しません。
                        ret = false;
                    }
                }
            } else {
                msg = Message.getString("fileprojectnewdialog.errordialog.message.xmlempty"); //中間コードが設定されていません。
                if (isSelectedSimpleMode()) {
                    msg = Message.getString("fileprojectnewdialog.errordialog.message.fortranempty"); //Fortranソースファイルが設定されていません。
                }
                ret = false;
            }
        }
        if (StringUtils.isNullOrEmpty(msg)) {
            //txaErrMsg.setText("");
            return ret;
        } else {
            //txaErrMsg.setText(msg);
            JOptionPane.showMessageDialog(this, msg,
                    Message.getString("fileprojectnewdialog.errordialog.title"), //設定エラー
                    JOptionPane.ERROR_MESSAGE);
            return ret;
        }
    }

    /**
     * ダイアログを表示する。
     *
     * @return ダイアログの閉じた時のボタン種別
     */
    public int showDialog() {

        // 親フレーム中央に表示する。
        this.setLocationRelativeTo(this.getOwner());

        // ダイアログ表示
        this.setVisible(true);

        return this.result;
    }

    /**
     * ボタンクリックイベント
     *
     * @param event	イベント情報
     */
    @Override
    public void actionPerformed(ActionEvent event) {

        // カレントフォルダ
        String currentFolder = this.txtProjectFolder.getText();
        if (currentFolder == null || currentFolder.isEmpty()) {
            if (this.lastAccessFolder != null) {
                currentFolder = this.lastAccessFolder;
            } else {
                currentFolder = System.getProperty("user.dir");
            }
        }
        // キャンセル
        if (event.getSource() == this.btnCancel) {
            this.result = Constant.CANCEL_DIALOG;
            // ダイアログを閉じる。
            dispose();
            return;
        }
        // プロジェクトフォルダの選択
        else if (event.getSource() == this.btnProjectFolder) {
            // フォルダ選択ダイアログを表示する。
            File[] selected = SwingUtils.showOpenFolderDialog(this,
                    Message.getString("dialog.common.selectproject.title"), //プロジェクトフォルダの選択
                    currentFolder, false);
            if (selected == null || selected.length <= 0) {
            	if (debug) System.out.println("Nothing selected by dialog.");
                return;
            }
            // 中間コードやMakefileが設定されている状態でプロジェクトフォルダが変更された場合は相対パスに注意
            String prjF = this.txtProjectFolder.getText();
            if (!StringUtils.isNullOrEmpty(prjF)) {
            	String prjFNew = selected[0].getAbsolutePath();
            	if (!prjF.equals(prjFNew)) {
            		DefaultListModel<String> model = (DefaultListModel<String>) this.listProjectXml.getModel();
            		String mc = null;
            		if (isGenerateIntermediateCode()) {
            			mc = this.txtBuildCommand.getText();
            		}
            		if (model.size() > 0 || !StringUtils.isNullOrEmpty(mc)) {
            			String desc = Message.getString("fileprojectnewdialog.confirmdialog.projectfolderchange.xmlclear.message"); //プロジェクトフォルダが変更されました。すでに設定されている中間コードは...
            			if (isSelectedSimpleMode()) {
            				desc = Message.getString("fileprojectnewdialog.confirmdialog.projectfolderchange.fortranclear.message"); //プロジェクトフォルダが変更されました。すでに設定されているフォートランファイルは...
            			} else {
            				if (!StringUtils.isNullOrEmpty(mc) && model.size() > 0) {
            					desc = Message.getString("fileprojectnewdialog.confirmdialog.projectfolderchange.makefileandxmlclear.message"); //プロジェクトフォルダが変更されました。すでに設定されているMakefileと中間コードは...
            				} else if (!StringUtils.isNullOrEmpty(mc)) {
            					desc = Message.getString("fileprojectnewdialog.confirmdialog.projectfolderchange.makefileclear.message"); //プロジェクトフォルダが変更されました。すでに設定されているMakefileは...
            				}
            			}

            			int ret = JOptionPane.showConfirmDialog(this, desc,
            					Message.getString("fileprojectnewdialog.confirmdialog.projectfolderchange.title"), //プロジェクトフォルダの変更
            					JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);
            			if (ret == JOptionPane.YES_OPTION) {
            				if (model.size() > 0) {
            					model.clear();
            				}
            			} else {
            				return;
            			}
            		}
            	}
            }

            // 選択プロジェクトフォルダが既存プロジェクトであるかチェックする。
            // プロジェクト設定ファイル
            File projectFile = new File(selected[0].getAbsolutePath() + File.separator + KscopeProperties.PROJECT_FILE);

            if (projectFile.exists()) {
                // 確認メッセージを表示する。
                int option = JOptionPane.showConfirmDialog(this,
                        Message.getString("fileprojectnewdialog.warnningdialog.projectexist.message"), //選択プロジェクトフォルダは既存プロジェクトです～
                        Message.getString("mainmenu.file.saveproject"), //プロジェクトの保存
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (option != JOptionPane.OK_OPTION) {
                    return;
                }
            }

            this.txtProjectFolder.setText(selected[0].getPath());
            enableButtons();

        }         
        else if (event.getSource() == this.txtProjectFolder) {
        	String folder = this.txtProjectFolder.getText();
        	if (debug_l2) System.out.println("Action on txtProjectFolder. Value: " + folder);
        	if (!StringUtils.isNullOrEmpty(folder)) {
        		try {
        			if (debug_l2) System.out.println("Test if " + folder+" is directory: ");
        			File f = new File(folder);
        			if (debug_l2) System.out.println(f.isDirectory());
        			if (f.isDirectory()) {
        				enableButtons();
        			} else {
        				System.out.println(folder + " is not a directory.");
        			}
        		} catch (NullPointerException e) {
        			e.printStackTrace();
        		}
        	}
        }
        // Add files to be preprocessed プレースホルダの処理対象のファイルを追加
        else if (event.getSource() == this.addprerocessfile_button) {
            // フォルダ選択ダイアログを表示する。
            File[] selected = SwingUtils.showOpenFileDialog(this, "Add preprocess files", currentFolder, null, true);
            if (selected == null || selected.length <= 0) {
                return;
            }
            for (File file : selected) {
                String path = "";
                try {
                    path = file.getCanonicalPath();
                    path = FileUtils.getRelativePath(this.txtProjectFolder.getText(), path);
                } catch (IOException e) {
                    return;
                }
                if (path.length() > 0) {
                    String preprocess_files = this.txt_preprocess_files.getText();
                    if (!StringUtils.isNullOrEmpty(preprocess_files)) {
                        preprocess_files = preprocess_files + ";" + path;
                    } else {
                        preprocess_files = path;
                    }
                    this.txt_preprocess_files.setText(preprocess_files);
                }
            }
        } 
        // フルモード: 中間コードフォルダ追加、簡易モード: Fortranコードフォルダの追加
        else if (event.getSource() == this.btnXmlFolder) {
            // フォルダ選択ダイアログを表示する。
            String title = null;
            if (this.radioFullMode.isSelected()) {
                title = Message.getString("fileprojectnewdialog.selectfolderdialog.xml.title"); //中間コードフォルダの選択
            } else if (this.radioSimpleMode.isSelected()) {
                title = Message.getString("fileprojectnewdialog.selectfolderdialog.fortran.title"); //Fortranフォルダの選択
            }
            File[] selected = SwingUtils.showOpenFolderDialog(this, title, currentFolder, true);
            if (selected == null || selected.length <= 0) {
                return;
            }

            // プロジェクトXMLファイルをリストに追加する
            addProjectList(selected);

        }
        // 中間コードファイル選択
        else if (event.getSource() == this.btnXmlFile) {
            // ファイル選択ダイアログのタイトル
            String title = null;
            SwingUtils.ExtFileFilter filter = null;
            if (this.radioFullMode.isSelected()) {
                title = Message.getString("fileprojectnewdialog.selectfiledialog.xml.title"); //中間コードファイルの選択
                // XMLファイルフィルタ
                // FILE_TYPEからフィルタ作成に変更 at 2013/05/14 by @hira
                // String description = Message.getString("fileprojectnewdialog.selectfolderdialog.xml.filter"); //XcodeMLファイル(*.xml)
                // String[] exts = {"xml"};
                // filter = new SwingUtils().new ExtFileFilter(description, exts);
                filter = new SwingUtils().new ExtFileFilter(FILE_TYPE.getXcodemlFilter());
            } else if (this.radioSimpleMode.isSelected()) {
                title = Message.getString("fileprojectnewdialog.selectfiledialog.fortran.title");//フォートランソースの選択
                // ソースファイルフィルタ
                // FILE_TYPEからフィルタ作成に変更 at 2013/05/14 by @hira
                // String description = Message.getString("fileprojectnewdialog.selectfolderdialog.fortran.filter"); //Fortranファイル(*.f,*.f90)
                // String[] exts = {"f", "f90"};
                // filter = new SwingUtils().new ExtFileFilter(description, exts);
                filter = new SwingUtils().new ExtFileFilter(FILE_TYPE.getFortranFilter());
            }

            // ファイル選択ダイアログを表示する。
            File[] selected = SwingUtils.showOpenFileDialog(this, title, currentFolder, filter, true);
            if (selected == null || selected.length <= 0) {
                return;
            }

            // プロジェクトXMLファイルをリストに追加する
            addProjectList(selected);

        }
        // 中間コードファイル削除
        else if (event.getSource() == this.btnXmlDelete) {
            int index = this.listProjectXml.getSelectedIndex();
            if (index < 0) {
                return;
            }
            DefaultListModel<String> model = (DefaultListModel<String>) this.listProjectXml.getModel();
            model.remove(index);
        }
        // ビルドコマンド参照ボタン
        else if (event.getSource() == this.btnMakeCmd) {
            File[] selected = SwingUtils.showOpenFileDialog(this,
                    Message.getString("fileprojectnewdialog.selectfiledialog.makecommand.title"), //ビルドコマンドの選択
                    currentFolder, null, false);
            if (selected == null || selected.length <= 0 || selected[0] == null) {
                return;
            }
            for (File file : selected) {
                String path = "";
                try {
                    path = file.getCanonicalPath();
                    path = FileUtils.getRelativePath(this.txtProjectFolder.getText(), path);
                } catch (IOException e) {
                    return;
                }
                if (path.length() > 0) {
                    String text_make_comm = this.txtBuildCommand.getText();
                    if (!StringUtils.isNullOrEmpty(text_make_comm)) {
                        text_make_comm = text_make_comm + " " + "./'" + path + "'";
                    } else {
                        text_make_comm = "./'" + path + "'";
                    }
                    this.txtBuildCommand.setText(text_make_comm);
                }
            }
        }
        // Clean command Refer button
        else if (event.getSource() == this.btnCleanCmd) {
        	File[] selected = SwingUtils.showOpenFileDialog(this,
                    Message.getString("fileprojectnewdialog.selectfiledialog.cleancommand.title"), // Dialog title for refer clean command
                    currentFolder, null, false);
            if (selected == null || selected.length <= 0 || selected[0] == null) {
                return;
            }
            for (File file : selected) {
                String path = "";
                try {
                    path = file.getCanonicalPath();
                    path = FileUtils.getRelativePath(this.txtProjectFolder.getText(), path);
                } catch (IOException e) {
                    return;
                }
                if (path.length() > 0) {
                    String text_clean_comm = this.txtCleanCommand.getText();
                    if (!StringUtils.isNullOrEmpty(text_clean_comm)) {
                    	text_clean_comm = text_clean_comm + " " + "./'" + path + "'";
                    } else {
                    	text_clean_comm = "./'" + path + "'";
                    }
                    this.txtCleanCommand.setText(text_clean_comm);
                }
            }
        }
        
        // 次へ
        else if (event.getSource() == this.btnNext) {
            if (!checkParams(this.wizardIndex)) {
                return;
            }
            // Save settings from Dialog to ProjectProperties instance
            if (this.wizardIndex == this.panelWizards.length - 1) {  
            	this.pproperties.setLocalPath(this.txtProjectFolder.getText());  // Project folder
            	if (this.checkUseRemote != null && this.checkUseRemote.isSelected()) {            		
	            	String settings_file = (String)this.settings_list.getSelectedItem();
	            	String remote_service = ProjectProperties.getRemoteService(settings_file);
	            	this.pproperties.setSettingsFile(settings_file);
	            	System.out.println("Remote service is set to "+remote_service);
	            	if (remote_service.equalsIgnoreCase(RemoteBuildProperties.REMOTE_SERVICE_SSHCONNECT)) {
		            	// Save File filter and Preprocess files fields to ProjectProperties
		            	if (this.txt_preprocess_files.getText().length() > 0) {
		            		pproperties.setPreprocessFiles(this.txt_preprocess_files.getText());
		            	}
		            	if (this.txt_filefilter.getText().length() > 0) {
		            		pproperties.setFileFilter(this.txt_filefilter.getText());
		            	}
	            	}            	
            	}
                // 入力チェックを行う
                if (!validateProject()) {
                    return;
                }

                //(2012/4/11) added by teraim パスが存在するか確認
                File f = new File(currentFolder);
                //そもそも存在しない。
                if (!f.exists()) {
                    f.mkdir();
                } else {
                    //存在するがファイルの場合（動作無保証、とりあえずエラー値を設定）
                    if (f.isFile()) {
                        this.result = Constant.ERROR_RESULT;
                    } //デフォルト（正常系）
                    else {
                        this.result = Constant.OK_DIALOG;
                    }
                }

                // ダイアログを閉じる。
                dispose();
                //ProjectBuildAction pb = new jp.riken.kscope.action.ProjectBuildAction();
                return;
            } else {
                if (this.wizardIndex < this.panelWizards.length - 1) {
                    this.wizardForward = this.wizardIndex;
                    this.wizardIndex++;
                    //System.out.println("Generate intercode:"+isGenerateIntermediateCode());
                    if (this.wizardIndex == 2 && !isGenerateIntermediateCode()) {
                        this.wizardIndex++;
                    }
                } else {
                    this.wizardIndex = this.panelWizards.length - 1;
                }
                changeWizardPage(this.wizardIndex);
            }
        }
        // 戻る
        else if (event.getSource() == this.btnBack) {
            this.wizardIndex = this.wizardForward;
            if (this.wizardForward > 0) {
                this.wizardForward--;
                if (this.wizardIndex == 2 && !isGenerateIntermediateCode()) {
                    this.wizardIndex--;
                    this.wizardForward--;
                }
            } else {
                this.wizardForward = 0;
            }
            changeWizardPage(this.wizardIndex);
        }
        // ラジオボタン　中間コード選択
        else if (event.getSource() == this.radioFullMode) {
            if (this.radioFullMode.isSelected()) {
            	if (radioGenXML.isSelected()) {
            	    // Enable checkUseRemote
            		if(this.pproperties.useRemoteBuild()) checkUseRemote.setEnabled(true);
            	}
                // すでにフォートランが設定されている場合はクリア
                DefaultListModel<String> model = (DefaultListModel<String>) this.listProjectXml.getModel();
                if (model.getSize() > 0) {
                    int res = JOptionPane.showConfirmDialog(this,
                            Message.getString("fileprojectnewdialog.confirmdialog.filekindchange.message"), //ファイル種別を変更するとファイルリストがクリアされます。ファイル種別を変更しますか？
                            Message.getString("fileprojectnewdialog.confirmdialog.filekindchange.title"), //ファイル種別の変更
                            JOptionPane.OK_CANCEL_OPTION);
                    if (res == JOptionPane.OK_OPTION) {
                        model.clear();
                        return;
                    } else {
                        this.radioSimpleMode.setSelected(true);
                        checkbox_StructureAnalysis.setSelected(false);
                        checkbox_StructureAnalysis.setEnabled(false);
                        return;
                    }
                }
            }
        }
        // ラジオボタン 簡易モード選択
        else if (event.getSource() == this.radioSimpleMode) {
            if (this.radioSimpleMode.isSelected()) {
            	// Disable checkUseRemote
            	if(this.pproperties.useRemoteBuild()) checkUseRemote.setEnabled(false);
                // すでに中間コードが設定されている場合はクリア
                DefaultListModel<String> model = (DefaultListModel<String>) this.listProjectXml.getModel();
                if (model.getSize() > 0) {
                    int res = JOptionPane.showConfirmDialog(this,
                            Message.getString("fileprojectnewdialog.confirmdialog.filekindchange.message"), //ファイル種別を変更するとファイルリストがクリアされます。ファイル種別を変更しますか？
                            Message.getString("fileprojectnewdialog.confirmdialog.filekindchange.title"), //ファイル種別の変更
                            JOptionPane.OK_CANCEL_OPTION);
                    if (res == JOptionPane.OK_OPTION) {
                        model.clear();
                        return;
                    } else {
                        this.radioFullMode.setSelected(true);
                        return;
                    }
                }
            }
        }        
        // Set Remote Build settings file path to ProjectProperties class instance pproperties
        else if (event.getSource() == this.settings_list) {
        	pproperties.setSettingsFile((String)this.settings_list.getSelectedItem());
        }
        // Set File Filter option to RBProperties
        else if (event.getSource() == this.txt_filefilter) {
        	pproperties.setFileFilter(this.txt_filefilter.getText());
        }
        // Set Preprocess files option to RBP
        else if (event.getSource() == this.txt_preprocess_files) {
        	pproperties.setPreprocessFiles(this.txt_preprocess_files.getText());
        	System.out.println("Checking RBdata after setting ppfiles to "+ this.txt_preprocess_files.getText());
        	pproperties.checkData();
        }
        else if (event.getSource() == this.manage_settings_files) {
        	if (debug) {
        		System.out.println("Button manage_settings_files pressed");
        	}
        	this.setModal(false);
        	ManageSettingsFilesDialog manage_files_dialog = new ManageSettingsFilesDialog();
        	manage_files_dialog.showDialog();   
        	refreshSettingsList();
        }
        if (debug_l2) System.out.println("actionPerformed() of FileProjectNewDialog exited");
    }
    
    /***
     * Refresh contents of settings_list JComboBox
     */
    private void refreshSettingsList() {
    	if (debug) System.out.println("Refireshing settings_list contents.");
    	String[] selections = ProjectProperties.getRemoteSettings();
    	if (debug_l2) System.out.println("selections: "+ selections.length);
    	// Save selected item
    	String selected = (String) this.settings_list.getSelectedItem();
    	this.settings_list.removeAllItems();
    	if (debug_l2) System.out.println("settings_list: removed all items, add following items:");
    	for (String s : selections) {    		
    		settings_list.addItem(s);
    		if (debug_l2) System.out.println(s);
    	}
    	// Restore selection in the drop-down list
    	if (selected != null && selected.length() > 1 )  this.settings_list.setSelectedItem(selected);
    	if (debug_l2) System.out.println("Restore selected item "+selected);
    }
    

    private void enableButtons() {
        this.btnNext.setEnabled(true);
        if (this.pproperties != null && this.pproperties.useRemoteBuild()) {
          //this.addprerocessfile_button.setEnabled(true);
        }
    }

    /**
     * プロジェクトファイルリスト重複チェック
     *
     * @param model プロジェクトファイルリスト
     * @param path 重複チェックプロジェクトファイル
     * @return true=重複ファイル
     */
    private boolean containsInProjectList(DefaultListModel<String> model, String path) {
        for (int i = 0; i < model.getSize(); i++) {
            String modelPath = model.getElementAt(i);
            if (modelPath.equals(path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * プロジェクト中間コードファイルを追加する
     *
     * @param file	XMLファイルリスト
     */
    private void addProjectList(File[] file) {
        if (file == null || file.length <= 0) {
            return;
        }

        // プロジェクトフォルダ
        String projectFolderName = this.txtProjectFolder.getText();
        File projectPath = null;
        if (projectFolderName != null && !projectFolderName.isEmpty()) {
            projectPath = new File(projectFolderName);
        }

        // 追加ファイルの重複チェックを行う
        List<File> selectedFiles = java.util.Arrays.asList(file);
        selectedFiles = new ArrayList<File>(selectedFiles);
        // プロジェクトXMLリストの作成
        DefaultListModel<String> model = (DefaultListModel<String>) this.listProjectXml.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            File path = null;
            try {
                String pathname = model.getElementAt(i);
                path = new File(pathname);
                if (!path.isAbsolute()) {
                    path = new File(projectPath.getCanonicalPath() + File.separator + pathname);
                }
            } catch (IOException ex) {
                path = null;
            }
            if (path == null) {
                continue;
            }

            if (selectedFiles.contains(path)) {
                selectedFiles.remove(path);
            }
        }

        // リストに追加する
        for (File addfile : selectedFiles) {

            try {

                // 除外パスに含まれているかチェックする
                if (this.containsExcludeName(addfile.getPath())) {
                    continue;
                }

                if (projectPath == null) {
                    // 絶対パス表示を行う
                    model.addElement(addfile.getCanonicalPath());
                } else if (projectPath.equals(addfile)) {
                    if (!containsInProjectList(model, "./")) {
                        model.addElement("./");
                    }                    
                } else if (FileUtils.isChildPath(projectPath.getCanonicalPath(), addfile.getCanonicalPath())) {
                    // 子パスであるので相対パス表示を行う
                    String relPath = FileUtils.getSubPath(addfile.getCanonicalPath(), projectPath.getCanonicalPath());
                    if (!containsInProjectList(model, relPath)) {
                        model.addElement(relPath);
                    }
                } else {
                    // 絶対パス表示を行う
                    model.addElement(addfile.getCanonicalPath());
                }
            } catch (IOException ex) {
            	if (debug_l2) System.err.println("Exception in addProjectList");
                ex.printStackTrace();
            }
        }

    }

    /**
     * 入力チェックを行う
     *
     * @return	true=入力チェックOK
     */
    private boolean validateProject() {

        String projectFolder = this.txtProjectFolder.getText();

        // プロジェクトフォルダ
        if (projectFolder == null || projectFolder.isEmpty()) {
            JOptionPane.showMessageDialog(this, Message.getString("fileprojectnewdialog.errordialog.noprojectfolder.messag"), //プロジェクトフォルダを選択してください。
                    Message.getString("dialog.common.error"), //エラー
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 中間コードファイルの存在チェックを行う。
        // プロジェクトフォルダを後で変更された場合、XMLファイルが存在しなくなる。
        List<File> list = getProjectXmlList();
        if (list != null) {
            for (File file : list) {
                if (!file.exists()) {
                    JOptionPane.showMessageDialog(this,
                            Message.getString("fileprojectnewdialog.errordialog.filenotexist.message", file.getName()), //ファイル[%s]が存在しません。
                            Message.getString("dialog.common.error"), //エラー
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 中間コードフォルダ・ファイルリストにアイコン表示クラス
     *
     * @author RIKEN
     *
     */

    private class IconListRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 8908060279825366210L;

		@Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value,
                int index, boolean isSelected,
                boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value,
                    index, isSelected,
                    cellHasFocus);
            String path = (String) value;
            File file = new File(path);
            if (!file.isAbsolute()) {
                try {
                    String projectFolder = getProjectFolder();
                    File projectPath = new File(projectFolder);
                    file = new File(projectPath.getCanonicalPath() + File.separator + path);
                } catch (IOException e) {
                }
            }

            String iconname = null;
            if (file.isDirectory()) {
                iconname = "folder.gif";
            } 
            else {
                iconname = "file.gif";
            }

            Icon icon = ResourceUtils.getIcon(iconname);
            // 表示アイコンを設定する。
            label.setIcon(icon);
            return label;
        }
    }

    /**
     * プロジェクトタイトルを取得する
     *
     * @return	プロジェクトタイトル
     */
    public String getPeojectTitle() {
        return this.txtProjectTitle.getText();
    }

    /**
     * プロジェクトフォルダを取得する
     *
     * @return	プロジェクトフォルダ
     */
    public String getProjectFolder() {
        return this.txtProjectFolder.getText();
    }

    /**
     * プロジェクト中間コードリストを取得する
     *
     * @return	プロジェクトXMLリスト
     */
    public List<File> getProjectXmlList() {
        // change from ListModel<?> for JDK1.6 by @hira at 2013/05/30
        ListModel<String> model = this.listProjectXml.getModel();
        if (model.getSize() <= 0) {
            return null;
        }

        // プロジェクトフォルダ
        String projectFolder = getProjectFolder();
        File projectPath = new File(projectFolder);

        // プロジェクト中間コードリストの作成
        List<File> list = new ArrayList<File>();
        for (int i = 0; i < model.getSize(); i++) {
            File path = null;
            try {
                String pathname = model.getElementAt(i);
                path = new File(pathname);
                if (!path.isAbsolute()) {
                    path = new File(projectPath.getCanonicalPath() + File.separator + pathname);
                }
            } catch (IOException ex) {
                path = null;
            }
            if (path == null) {
                continue;
            }
            list.add(path);
        }

        if (list.size() <= 0) {
            return null;
        }

        return list;
    }

    /**
     * 除外パス名を設定する
     *
     * @param name	除外パス名
     */
    public void addExcludeName(String name) {
        if (this.excludeName == null) {
            this.excludeName = new ArrayList<String>();
        }
        this.excludeName.add(name);
    }

    /**
     * 除外パス名に含まれているかチェックする
     *
     * @param path	検索パス名
     * @return	true=除外パス
     */
    private boolean containsExcludeName(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        if (this.excludeName == null) {
            return false;
        }

        path = path.toLowerCase();
        for (String value : this.excludeName) {
            value = value.toLowerCase();
            if (path.endsWith(value)) {
                return true;
            }

        }
        return false;
    }

    /**
     * 最終アクセスフォルダを設定する
     *
     * @param folder	最終アクセスフォルダ
     */
    public void setLastAccessFolder(String folder) {
        if (folder == null) {
            return;
        }
        this.lastAccessFolder = folder;
    }

    /**
     * Set Text Field text to project title
     *
     * @param title	プロジェクトタイトル
     */
    public void setProjectTitle(String title) {
        this.txtProjectTitle.setText(title);
    }
    
    /**
     * ビルドコマンド設定
     *
     * @param	str	ビルドコマンド
     */
    public void setBuildCommand(String str) {
        this.txtBuildCommand.setText(str);
    }

    /**
     * ビルドコマンド取得
     *
     * @return String としてのbuildコマンド
     */
    public String getBuildCommand() {
        return this.txtBuildCommand.getText();
    }
    
    /**
     * Return clean command
     *
     * @return clean command as a String
     */
    public String getCleanCommand() {
        return this.txtCleanCommand.getText();
    }
    
    /**
     * Return value of settings_file ComboBox (Drop-down list) 
     */
    public String getSettingsFile() {
    	return (String)this.settings_list.getSelectedItem();
    	//return this.pproperties.getRemoteSettingsFile();
    }
    
     /**
     * 選択ファイルが中間コードファイルであるかチェックする。
     *
     * @return	true=XMLファイル
     */
    public boolean isSelectedXml() {
        return this.radioFullMode.isSelected();
    }

    /**
     * 選択ファイルがFortranファイルであるかチェックする。
     *
     * @return	true=Fortranファイル
     */
    public boolean isSelectedSimpleMode() {
        return this.radioSimpleMode.isSelected();
    }

    /**
     * 中間コードの生成を行うかチェックする
     *
     * @return	true=中間コードの生成を行う
     */
    public boolean isGenerateIntermediateCode() {
        if (this.radioGenXML.isEnabled()) {
            return this.radioGenXML.isSelected();
        }
        return false;
    }

    /**
     * Where source code must be built.
     *
     * @return remote settings file path -- if build on remote server,
     * null if build locally.
     */
    public String remoteSettingsFile() {
        if (this.checkUseRemote == null) {
            return null;
        }
        if (this.checkUseRemote.isEnabled()) {
            if (this.checkUseRemote.isSelected()) {
            	String settings_file=(String)this.settings_list.getSelectedItem();
            	if (settings_file != null && settings_file.length() > 0) {
            		return settings_file;
            	}
            }
        }
        return null;
    }

    /**
     * プロジェクト作成直後に保存フラグを設定する
     *
     * @param	save true=保存する
     */
    public void setSaveFlag(boolean save) {
        cbxSaveProject.setSelected(save);
    }

    /**
     * プロジェクト作成後にプロジェクトを保存するかチェックする
     *
     * @return	true=保存する
     */
    public boolean isSave() {
        return this.cbxSaveProject.isSelected();
    }

    /**
     * 構造解析を自動的に行うかチェックする
     *
     * @return	true=構造解析を行う
     */
    public boolean isBuild() {
        if (isSelectedSimpleMode()) {
            return false;
        }
        return this.checkbox_StructureAnalysis.isSelected();
    }
}

