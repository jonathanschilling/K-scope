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
package jp.riken.kscope.gui;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jp.riken.kscope.common.KEYWORD_TYPE;
import jp.riken.kscope.data.CodeLine;
import jp.riken.kscope.data.Keyword;
import jp.riken.kscope.data.SourceFile;
import jp.riken.kscope.menu.SourcePanelPopupMenu;
import jp.riken.kscope.model.SourceCodeModel;
import jp.riken.kscope.properties.KeywordProperties;
import jp.riken.kscope.properties.ProfilerProperties;
import jp.riken.kscope.properties.SourceProperties;
import jp.riken.kscope.properties.VariableMemoryProperties;


/**
 * ルーラ付きソースコード表示パイン.<br/>
 * 右側にプロファイラプレビューパネルを表示する.
 * @author RIKEN
 */
public class SourceCodePanel extends JPanel implements ITabComponent, ChangeListener, CaretListener, Observer {

    /** シリアル番号 */
    private static final long serialVersionUID = 1L;
    /** ソースコードパネル */
    private ScrollCodePane panelCode;
    /** プロファイラルーラパネル */
    private ProfilerRulerPanel rulerProfiler;
    /** 親コンポーネント */
    private ITabComponent parentCompornent = null;

    /**
     * コンストラクタ
     */
    public SourceCodePanel() {
        super();
        initGUI();
    }

    /**
     * 初期化を行う.<br/>
     * ソースコードパネルを配置する。
     */
    private void initGUI() {
        try {
            BorderLayout thisLayout = new BorderLayout();
            this.setLayout(thisLayout);

            // ソースコード表示パイン
            {
            	this.panelCode = new ScrollCodePane();
                this.add(this.panelCode, BorderLayout.CENTER);
            }
            // プロファイラルーラパネル
            {
            	this.rulerProfiler = new ProfilerRulerPanel(this.panelCode);
                this.add(this.rulerProfiler, BorderLayout.EAST);
            }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
    }

    /**
     * ソースビューモデルの変更通知イベント
     * @param o			通知元
     * @param arg		通知項目
     */
	@Override
	public void update(Observable o, Object arg) {
		this.panelCode.update(o, arg);
		this.rulerProfiler.update(o, arg);
	}

    /**
     * キャレット位置更新イベント
     * @param event		イベント情報
     */
	@Override
	public void caretUpdate(CaretEvent event) {
		this.panelCode.caretUpdate(event);
	}

    /**
     * スクロール変更イベント
     * @param event		イベント情報
     */
	@Override
	public void stateChanged(ChangeEvent event) {
		this.panelCode.stateChanged(event);
	}

    /**
     * 親コンポーネントを取得する.
     * @return		親コンポーネント
     */
    @Override
    public ITabComponent getParentComponent() {
        return this.parentCompornent;
    }

    /**
     * 親コンポーネントを設定する.
     * @param component		親コンポーネント
     */
    @Override
    public void setParentComponent(ITabComponent component) {
        this.parentCompornent = component;
    }

    /**
     * フォーカスリスナを設定する
     * @param listener		フォーカスリスナ
     */
    @Override
    public void addTabFocusListener(TabFocusListener listener) {
        this.addFocusListener(listener);
        if (this.panelCode != null) {
            this.panelCode.addTabFocusListener(listener);
        }
    }

    /**
     * タブを閉じる
     */
    @Override
    public void closeTabComponent() {
        // 親のタブパインにてタブを閉じる。
        if (this.parentCompornent != null) {
            this.parentCompornent.closeTabComponent();
        }
    }

    /**
     * ソースファイルを読み込む
     * @param source		ソースファイル
     * @throws Exception		ソースファイル読込エラー
     */
    public void readFile(SourceFile source) throws Exception {
		this.panelCode.readFile(source);
		this.rulerProfiler.addObserver();
    }

    /**
     * ソースファイルパネルコンテキストメニューを設定する
     * @param menuSourcePanel		ソースファイルパネルコンテキストメニュー
     */
    public void setSourcePanelPopupMenu(SourcePanelPopupMenu menuSourcePanel) {
        this.panelCode.setSourcePanelPopupMenu(menuSourcePanel);
    }

    /**
     * テキストパインを取得する。
     * @return		テキストパイン
     */
    public CodePane getSourcePane() {
        return this.panelCode.getSourcePane();
    }

    /**
     * ソースビュープロパティを設定する
     * @param properties		ソースビュープロパティ
     */
    public void setSourceProperties(SourceProperties properties) {
        this.panelCode.setSourceProperties(properties);
    }

    /**
     * キーワードプロパティを設定する
     * @param properties		キーワードプロパティ
     */
    public void setKeywordProperties(KeywordProperties properties) {
        this.panelCode.setKeywordProperties(properties);
    }

    /**
     * ソースコードモデルを取得する
     * @return			ソースコードモデル
     */
    public SourceCodeModel getModel() {
        return this.panelCode.getModel();
    }

    /**
     * 表示ソースファイルパス（絶対パス)を取得する。
     * @return filePath		表示ソースファイルパス（絶対パス)
     */
    public String getFilePath() {
        return this.panelCode.getFilePath();
    }

    /**
     * コード行情報の選択範囲をクリアする。
     */
    public void clearSelectedBlock() {
    	this.panelCode.clearSelectedBlock();
    }

    /**
     * コード行情報の選択範囲を追加する
     * @param line		コード行情報
     */
    public void addSelectedBlock(CodeLine line) {
    	this.panelCode.addSelectedBlock(line);
    }

    /**
     * 指定行番号位置を表示領域に表示する。
     * @param line		表示行番号
     */
    public void setLinePosition(CodeLine line) {
    	this.panelCode.setLinePosition(line);
    }

    /**
     * 指定行番号位置を表示領域に表示する。
     * @param start		表示行番号
     */
    public void setLinePosition(int start) {
    	this.panelCode.setLinePosition(start);
    }

    /**
     * 選択行、選択文字情報を取得する
     * @return		選択行情報
     */
    public CodeLine getSelectedCodeLine() {
    	return this.panelCode.getSelectedCodeLine();
    }

    /**
     * 選択行範囲を取得する
     * @return		選択範囲行コード情報
     */
    public CodeLine getSelectedArea() {
    	return this.panelCode.getSelectedArea();
    }

    /**
     * ソースファイルを取得する
     * @return		ソースファイル
     */
    public SourceFile getSelectedSourceFile() {
    	return this.panelCode.getSelectedSourceFile();
    }

    /**
     * 検索・トレースキーワードを設定する
     * @param keywords		検索・トレースキーワード
     */
    public void setSearchWords(Keyword[] keywords) {
    	this.panelCode.setSearchWords(keywords);
    }

    /**
     * 検索・トレースキーワードをクリアする.
     */
    public void clearSearchWords() {
    	this.panelCode.clearSearchWords();
    }

    /**
     * 検索・トレースキーワードをクリアする
     * @param  type     クリアキーワードタイプ
     */
    public void clearSearchWords(KEYWORD_TYPE type) {
    	this.panelCode.clearSearchWords(type);
    }

    /**
     * バーグラフデータをクリアする。
     */
    public void clearBargraphData() {
        this.panelCode.clearBargraphData();
        this.rulerProfiler.clearProfilerData();
    }

    /**
     * 現在選択されているテキストをクリップボードにコピーする
     */
    public void copyClipboard() {
    	this.panelCode.copyClipboard();
    }

    /**
     * プロファイラデータ表示フッターを表示する
     * @param visible		true=表示
     */
    public void setVisibleBargraph(boolean visible) {
    	this.panelCode.setVisibleBargraph(visible);
    }

    /**
     * プロファイラデータ表示ルーラを表示する
     * @param visible		true=表示
     */
    public void setVisibleRuler(boolean visible) {
    	this.rulerProfiler.setVisible(visible);
    }

    /**
     * プロファイラプロパティを設定する
     * @param properties	プロファイラプロパティ
     */
	public void setProfilerProperties(ProfilerProperties properties) {
        // コストバーグラフ表示切替
        boolean visibleBargraph = properties.isVisibleBargraph();
    	// 表示切替及び再描画を行う.
        setVisibleBargraph(visibleBargraph);
        // コストルーラ表示切替
        boolean visibleRuler = properties.isVisibleRuler();
        setVisibleRuler(visibleRuler);
        // プロファイラプロパティを設定する
        this.rulerProfiler.setProfilerProperties(properties);
	}

    /**
     * 変数アクセス先メモリプロパティを設定する
     * @param properties	変数アクセス先メモリプロパティ
     */
	public void setVariableMemoryProperties(VariableMemoryProperties properties) {
        this.panelCode.setVariableMemoryProperties(properties);
	}

	/**
	 * 選択行を追加する.<br/>
	 * キャレットの移動は行わない.
	 * @param line		コード行情報
	 */
	public void addSelectedBlockNoCaret(CodeLine line) {
    	this.panelCode.addSelectedBlockNoCaret(line);
	}

	/**
	 * キーワードのハイライトを適用する.
	 */
	public void applyKeyword() {
		this.panelCode.applyKeyword();
	}

}

