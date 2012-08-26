package com.bendod.androlbg;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import com.bendod.androlbg.activity.AbstractActivity;
import com.bendod.androlbg.connector.OLBGParser;
import com.bendod.androlbg.enumerations.StatusCode;
import com.bendod.androlbg.utils.Utils;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.app.ProgressDialog;

import com.actionbarsherlock.view.Window;
import android.content.Context;

public class MakeTipActivity extends AbstractActivity {
	
	private List<LabelValuePair> search;
	private Tip tip;
	private ProgressDialog confirmTipDialog = null;
		
	private Handler loadEventsHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
        	Spinner spinner = (Spinner) findViewById(R.id.event_id);
			if("Error!".equals(search.get(0).getValue())){
				showToast(search.get(0).toString());
				finish();
				return;
			}
			tip.sport = search.remove(0).toString();
			tip.subSport = search.remove(0).toString();
        	tip.feedTable = search.remove(0).toString();
			ArrayAdapter<LabelValuePair> eventsAdapter = new ArrayAdapter<LabelValuePair>(app.getBaseContext(), 
    				android.R.layout.simple_spinner_item, search);
        	eventsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(eventsAdapter);
            spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            		tip.eventId = ((LabelValuePair)parent.getItemAtPosition(pos)).getValue();
					((EditText) findViewById(R.id.comment_id)).setText("");
					((EditText) findViewById(R.id.stake_id)).setText("");
                    if(Utils.isBlank(tip.eventId)){
                    	Spinner nextSpinner;
                    	if(tip.subSport.equals("Horse Racing")){
                    		nextSpinner = (Spinner) findViewById(R.id.selection_id);
                    	}else{
                    		nextSpinner = (Spinner) findViewById(R.id.market_id);
                    	}
						ArrayAdapter<CharSequence> defAdapter = ArrayAdapter.createFromResource(app.getBaseContext(), 
								R.array.market_spinner, android.R.layout.simple_spinner_item);
						defAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						nextSpinner.setAdapter(defAdapter);
					}else{
						setSupportProgressBarIndeterminateVisibility(true);
						if(tip.subSport.equals("Horse Racing")){
							tip.marketId = 81;
							SelectionsThread thread = new SelectionsThread();
		                    thread.start();
						}else{
							MarketsThread thread = new MarketsThread();
	                        thread.start();						
	                    }
                    }
                }

                public void onNothingSelected(AdapterView<?> parent) {
                	// TODO Auto-generated method stub
                }
			});
			setSupportProgressBarIndeterminateVisibility(false);
        }
	};
	
	private Handler loadMarketsHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
        	Spinner spinner = (Spinner) findViewById(R.id.market_id);
        	ArrayAdapter<LabelValuePair> marketsAdapter = new ArrayAdapter<LabelValuePair>(app.getBaseContext(), 
    				android.R.layout.simple_spinner_item, search);
        	marketsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(marketsAdapter);
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            		Object slctdItem = parent.getItemAtPosition(pos);
					String marketId = null;
					if(slctdItem instanceof LabelValuePair) {
						marketId = ((LabelValuePair) slctdItem).getValue();
					}
                    if(Utils.isBlank(marketId)){
						tip.marketId = -1;
						Spinner nextSpinner = (Spinner) findViewById(R.id.selection_id);
						ArrayAdapter<CharSequence> defAdapter = ArrayAdapter.createFromResource(app.getBaseContext(), 
								R.array.selection_spinner, android.R.layout.simple_spinner_item);
						defAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						nextSpinner.setAdapter(defAdapter);
					}else{
						tip.marketId = Integer.parseInt(marketId);
                    	setSupportProgressBarIndeterminateVisibility(true);
						SelectionsThread thread = new SelectionsThread();
                        thread.start();
                    }
                }

                public void onNothingSelected(AdapterView<?> parent) {
                	// TODO Auto-generated method stub
                }
			});
			setSupportProgressBarIndeterminateVisibility(false);
        }
	};
	
	private Handler loadSelectionsHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			Spinner spinner = (Spinner) findViewById(R.id.selection_id);
			tip.course = search.remove(0).toString();
			ArrayAdapter<LabelValuePair> selectionsAdapter = new ArrayAdapter<LabelValuePair>(app.getBaseContext(), 
						android.R.layout.simple_spinner_item, search);
			selectionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(selectionsAdapter);
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
     		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
					Object slctdItem = parent.getItemAtPosition(pos);
					String selectionId = null;
					if(slctdItem instanceof LabelValuePair) {
						selectionId = ((LabelValuePair) slctdItem).getValue();
					}
					tip.selection = selectionId;
                    if(Utils.isBlank(selectionId)){
						Spinner nextSpinner = (Spinner) findViewById(R.id.bet_type_id);
						ArrayAdapter<CharSequence> defAdapter = ArrayAdapter.createFromResource(app.getBaseContext(), 
								R.array.bet_type_spinner, android.R.layout.simple_spinner_item);
						defAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						nextSpinner.setAdapter(defAdapter);
						if(tip.subSport.equals("Horse Racing")){
							Spinner nextSpinner2 = (Spinner) findViewById(R.id.odds_id);
							ArrayAdapter<CharSequence> defAdapter2 = ArrayAdapter.createFromResource(app.getBaseContext(), 
									R.array.bet_type_spinner, android.R.layout.simple_spinner_item);
							defAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
							nextSpinner2.setAdapter(defAdapter2);
						}
					}else{
						setSupportProgressBarIndeterminateVisibility(true);
            	        BetTypeThread thread = new BetTypeThread();
                        thread.start();
                    }
                }

            	    public void onNothingSelected(AdapterView<?> parent) {
          		      	// TODO Auto-generated method stub:
            	    }
				});
			setSupportProgressBarIndeterminateVisibility(false);
		}
	};
	
	private Handler loadBetTypeHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			Spinner spinner = (Spinner) findViewById(R.id.bet_type_id);
			ArrayAdapter<LabelValuePair> betTypeAdapter = new ArrayAdapter<LabelValuePair>(app.getBaseContext(), 
						android.R.layout.simple_spinner_item, search);
			betTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(betTypeAdapter);
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
     		       	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            			Object slctdItem = parent.getItemAtPosition(pos);
						String betTypeId = null;
						if(slctdItem instanceof LabelValuePair) {
							betTypeId = ((LabelValuePair) slctdItem).getValue();
						}
						tip.betType = betTypeId;
                	}

            	    public void onNothingSelected(AdapterView<?> parent) {
          		      	// TODO Auto-generated method stub
            	    }
			});
			if(tip.subSport.equals("Horse Racing")){
				BetOddsThread thread = new BetOddsThread();
                thread.start();
			}else{
				tip.price = "Take Price";
				setSupportProgressBarIndeterminateVisibility(false);
			}
		}
	};
	
	private Handler loadBetOddsHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			Spinner spinner = (Spinner) findViewById(R.id.odds_id);
			ArrayAdapter<LabelValuePair> betOddsAdapter = new ArrayAdapter<LabelValuePair>(app.getBaseContext(), 
						android.R.layout.simple_spinner_item, search);
			betOddsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(betOddsAdapter);
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
     		       	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            			Object slctdItem = parent.getItemAtPosition(pos);
						String betOddsId = null;
						if(slctdItem instanceof LabelValuePair) {
							betOddsId = ((LabelValuePair) slctdItem).getValue();
						}
						tip.price = betOddsId;
                	}

            	    public void onNothingSelected(AdapterView<?> parent) {
          		      	// TODO Auto-generated method stub
            	    }
				});
			setSupportProgressBarIndeterminateVisibility(false);
		}
	};
	
	private Handler loadConfirmTipHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(confirmTipDialog != null && confirmTipDialog.isShowing()) {
				confirmTipDialog.dismiss();
			}
			if((StatusCode) msg.obj == StatusCode.TIP_SAVED) {
				((Spinner) findViewById(R.id.event_id)).setSelection(0);
				helpDialog("", res.getString(R.string.info_tip_saved));
				//Login.
			}else{
				helpDialog("", res.getString(R.string.err_tip_post_failed));
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setTheme();
		super.onCreate(savedInstanceState);
                
        Intent intent = getIntent();
		int tipId = intent.getIntExtra(TipsMenuActivity.TIPS_MENU_ITEM,0);
		int tipTitle = intent.getIntExtra(TipsMenuActivity.TIPS_MENU_TITLE,0);
		tip = new Tip();
		actionBar.setTitle(tipTitle);
		
		setContentView(R.layout.make_tip);
		
		btnInit();
		if(tipId == 2) {
			horseRacingInit();
		}
		
		setSupportProgressBarIndeterminateVisibility(true);
				
        EventsThread thread = new EventsThread(this, tipId);
        thread.start();
        
    }
	
	private void btnInit() {
		final Button confirmTipBtn = (Button) findViewById(R.id.confirm_tip_btn);
		confirmTipBtn.setOnClickListener(new confirmTip());
	}
    
	private void horseRacingInit() {
		findViewById(R.id.make_nap).setVisibility(View.VISIBLE);
		findViewById(R.id.chk_nap_nb).setVisibility(View.VISIBLE);
		findViewById(R.id.bet_odds).setVisibility(View.VISIBLE);
		findViewById(R.id.odds_id).setVisibility(View.VISIBLE);
		findViewById(R.id.market_id).setVisibility(View.GONE);
		findViewById(R.id.pick_market).setVisibility(View.GONE);
	}
    
    private class EventsThread extends Thread {
    	final private Handler handler;
    	final private int tipId;
		final private Context context;
        
        public EventsThread(final Context context, final int tipId) {
            this.handler = loadEventsHandler;
            this.tipId = tipId;
			this.context = context;
        }
        
        @Override
        final public void run() {
            super.run();
            try {
				runSearch();
			} catch (IOException e) {
			} catch (URISyntaxException e) {
			}
            handler.sendMessage(Message.obtain());
        }
         
        public void runSearch() throws IOException, URISyntaxException {
            search = OLBGParser.searchById(context, tipId);
        }
            
    }
    
    private class MarketsThread extends Thread {
    	final private Handler handler;
        
    	public MarketsThread() {
    		this.handler = loadMarketsHandler;
        }

        @Override
        final public void run() {
            super.run();
            try {
				runSearch();
			} catch (IOException e) {
			} catch (URISyntaxException e) {
			}
            handler.sendMessage(Message.obtain());
        }
         
        public void runSearch() throws IOException, URISyntaxException {
            search = OLBGParser.searchByEvent(tip.eventId, tip.feedTable);
        }
        
    }
		
	private class SelectionsThread extends Thread {
    	final private Handler handler;

    	public SelectionsThread() {
    		this.handler = loadSelectionsHandler;
        }

        @Override
        final public void run() {
            super.run();
            try {
				runSearch();
			} catch (IOException e) {
			} catch (URISyntaxException e) {
			}
            handler.sendMessage(Message.obtain());
        }

        public void runSearch() throws IOException, URISyntaxException {
            search = OLBGParser.searchByMarket(tip.marketId, tip.eventId, tip.feedTable);
        }

    }

	private class BetTypeThread extends Thread {
    	final private Handler handler;

    	public BetTypeThread() {
    		this.handler = loadBetTypeHandler;
        }

        @Override
        final public void run() {
            super.run();
            try {
				runSearch();
			} catch (IOException e) {
			} catch (URISyntaxException e) {
			}
            handler.sendMessage(Message.obtain());
        }

        public void runSearch() throws IOException, URISyntaxException {
            search = OLBGParser.searchBySelection(tip.selection, tip.eventId, tip.subSport, "bettype");
        }

    }
	
	private class BetOddsThread extends Thread {
    	final private Handler handler;

    	public BetOddsThread() {
    		this.handler = loadBetOddsHandler;
        }

        @Override
        final public void run() {
            super.run();
            try {
				runSearch();
			} catch (IOException e) {
			} catch (URISyntaxException e) {
			}
            handler.sendMessage(Message.obtain());
        }

        public void runSearch() throws IOException, URISyntaxException {
            search = OLBGParser.searchBySelection(tip.selection, tip.eventId, tip.subSport, "price");
        }

    }
	
	private class confirmTip implements View.OnClickListener {
		
		@Override
		public void onClick(View v) {
			tip.comment = ((EditText) findViewById(R.id.comment_id)).getText().toString();
			tip.nap = ((CheckBox) findViewById(R.id.chk_nap)).isChecked();
			tip.nb = ((CheckBox) findViewById(R.id.chk_nb)).isChecked();
			String stakeStr = ((EditText)findViewById(R.id.stake_id)).getText().toString();
			String firstError = tip.validateTip(stakeStr);
			if(!Utils.isBlank(firstError)){
				showToast(firstError);
				return;
			}
			//tip.setStake(Integer.parseInt(stakeStr));

			confirmTipDialog = ProgressDialog.show(MakeTipActivity.this, "", "", true);
			confirmTipDialog.setCancelable(false);
			
			(new Thread() {
				
				@Override
				public void run() {
					StatusCode tipStatusCode = StatusCode.UNKNOWN_ERROR;
					try {
						tipStatusCode = OLBGParser.confirmTip(tip);
					} catch (IOException e) {
					} catch (URISyntaxException e) {
					}
					loadConfirmTipHandler.obtainMessage(0, tipStatusCode).sendToTarget();
				}
			}).start();
		}
       

    }
	
}
