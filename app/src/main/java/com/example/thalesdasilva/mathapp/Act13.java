package com.example.thalesdasilva.mathapp;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.thalesdasilva.mathapp.app.MessageBox;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Thales da Silva Neves (thales.neves@fatec.sp.gov.br)
 * @since Classe criada em 08/01/2018
 */

public class Act13 extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {

    public static TextView      TXT_NUM_1;
    public static TextView      TXT_NUM_2;
    public static TextView      TXT_SINAL;
    public static TextView      TXT_TEMPO;
    public static TextView      TXT_PONTUACAO;
    public static TextToSpeech  TEXT_TO_SPEECH;
    public static Timer         TIMER;
    public static Float         RESULTADO_CORRETO = 0f;
    public static DecimalFormat DF = new DecimalFormat("#.##");
    public static Integer       NUMERO_RANDOMICO_2 = 0;
    public static Float         NUMERO_RANDOM_FLOAT = 0f;
    public static Boolean       VERIFICAR_ONRESUME = Boolean.FALSE;

    private Button btnResult1;
    private Button btnResult2;
    private Button btnResult3;
    private Button btnResult4;
    private Button btnParar;
    private Button btnRepetir;

    private Random random = new Random();

    private Integer numeroAleatorioParaAEscolhaDeQualBotaoASerPreenchido = random.nextInt(4) + 1;
    private Integer numeroRandomico1 = 0;
    private Integer seconds = 30;

    private Boolean voltarParaOMenuDeTreinamento = Boolean.FALSE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_13);

        getSupportActionBar().hide();

        recuperandoReferencia();

        TEXT_TO_SPEECH = new TextToSpeech(this,
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            TEXT_TO_SPEECH.setLanguage(Locale.getDefault());

                            if (TXT_SINAL.getText().toString().equals(getString(R.string.mais))) {
                                TEXT_TO_SPEECH.speak("Qual valor somado a " + TXT_NUM_1.getText().toString() +
                                                " da o resultado de " + String.valueOf(RESULTADO_CORRETO).replace(",0", ""),
                                        TextToSpeech.QUEUE_FLUSH, null);
                            } else if (TXT_SINAL.getText().toString().equals(getString(R.string.menos))) {
                                TEXT_TO_SPEECH.speak("Qual valor subtraído a " + TXT_NUM_1.getText().toString() +
                                                " da o resultado de " + String.valueOf(RESULTADO_CORRETO).replace(",0", ""),
                                        TextToSpeech.QUEUE_FLUSH, null);
                            } else if (TXT_SINAL.getText().toString().equals(getString(R.string.porcentagem))) {
                                TEXT_TO_SPEECH.speak("Qual valor em porcentagem multiplicado a " + TXT_NUM_1.getText().toString() +
                                                " da o resultado de " + String.valueOf(RESULTADO_CORRETO).replace(",0", ""),
                                        TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }
                    }
                });

        VERIFICAR_ONRESUME = Boolean.FALSE;
        voltarParaOMenuDeTreinamento = Boolean.FALSE;

        carregarEstrutura();
        iniciarTempo();
        ouvintes();

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            String pontos = bundle.getString("TXT_PONTUACAO");
            TXT_PONTUACAO.setText(String.valueOf(pontos));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (!btnParar.isPressed() && Boolean.FALSE == voltarParaOMenuDeTreinamento && Boolean.FALSE == VERIFICAR_ONRESUME) {
            irParaActivityPausa13();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (TXT_SINAL.getText().toString().equals(getString(R.string.mais))) {
            TEXT_TO_SPEECH.speak("Qual valor somado a " + TXT_NUM_1.getText().toString() +
                            " da o resultado de " + String.valueOf(RESULTADO_CORRETO).replace(",0", ""),
                    TextToSpeech.QUEUE_FLUSH, null);
        } else if (TXT_SINAL.getText().toString().equals(getString(R.string.menos))) {
            TEXT_TO_SPEECH.speak("Qual valor subtraído a " + TXT_NUM_1.getText().toString() +
                            " da o resultado de " + String.valueOf(RESULTADO_CORRETO).replace(",0", ""),
                    TextToSpeech.QUEUE_FLUSH, null);
        } else if (TXT_SINAL.getText().toString().equals(getString(R.string.porcentagem))) {
            TEXT_TO_SPEECH.speak("Qual valor em porcentagem multiplicado a " + TXT_NUM_1.getText().toString() +
                            " da o resultado de " + String.valueOf(RESULTADO_CORRETO).replace(",0", ""),
                    TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onBackPressed() {
        TEXT_TO_SPEECH.speak("Voltar para o menu de treinamento", TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        TIMER.cancel();
        voltarParaOMenuDeTreinamento = Boolean.TRUE;
        bloquearBotoesResultados();
        TEXT_TO_SPEECH.speak("Voltando para o menu de treinamento", TextToSpeech.QUEUE_FLUSH, null);

        boolean speakingEnd = TEXT_TO_SPEECH.isSpeaking();

        do {
            speakingEnd = TEXT_TO_SPEECH.isSpeaking();
        } while (speakingEnd);

        Intent it = new Intent(Act13.this, ActTreinamento.class);
        startActivity(it);

        return true;
    }

    public void carregarEstrutura() {
        desbloquearBotoesResultados();

        int numeroRandom = random.nextInt(3) + 1;

        if (numeroRandom == 1) {
            TXT_SINAL.setText(String.valueOf(getString(R.string.mais)));
        } else if (numeroRandom == 2) {
            TXT_SINAL.setText(String.valueOf(getString(R.string.menos)));
        } else if (numeroRandom == 3) {
            TXT_SINAL.setText(String.valueOf("%"));
        }

        numeroRandomico1 = (int) (Math.random() * 10);
        NUMERO_RANDOMICO_2 = (int) (Math.random() * 10);

        if (numeroRandomico1 == 0) {
            numeroRandomico1++;
        }

        if (NUMERO_RANDOMICO_2 == 0) {
            NUMERO_RANDOMICO_2++;
        }

        if ((TXT_SINAL.getText().toString().equals(getString(R.string.mais))) || (TXT_SINAL.getText().toString().equals(getString(R.string.menos)))) {
            if (TXT_SINAL.getText().toString().equals(getString(R.string.mais))) {
                RESULTADO_CORRETO = Float.valueOf(numeroRandomico1 + NUMERO_RANDOMICO_2);
            } else if (TXT_SINAL.getText().toString().equals(getString(R.string.menos))) {
                RESULTADO_CORRETO = Float.valueOf(numeroRandomico1 - NUMERO_RANDOMICO_2);
            }

            String RESULTADO_CORRETOFormatado = String.format(String.valueOf(DF.format(RESULTADO_CORRETO))).replace(".0", "");

            TXT_NUM_1.setText(String.valueOf(numeroRandomico1));
            TXT_NUM_2.setText(String.valueOf(RESULTADO_CORRETOFormatado));

            if (TXT_SINAL.getText().toString().equals(getString(R.string.mais))) {
                TEXT_TO_SPEECH.speak("Qual valor somado a " + String.valueOf(numeroRandomico1) +
                                " da o resultado de " + String.valueOf(RESULTADO_CORRETOFormatado),
                        TextToSpeech.QUEUE_FLUSH, null);
            } else if (TXT_SINAL.getText().toString().equals(getString(R.string.menos))) {
                TEXT_TO_SPEECH.speak("Qual valor subtraído a " + String.valueOf(numeroRandomico1) +
                                " da o resultado de " + String.valueOf(RESULTADO_CORRETOFormatado),
                        TextToSpeech.QUEUE_FLUSH, null);
            }

            int numeroRandomicoBtnResult1 = 0;
            int numeroRandomicoBtnResult2 = 0;
            int numeroRandomicoBtnResult3 = 0;
            int numeroRandomicoBtnResult4 = 0;

            if (RESULTADO_CORRETO <= 9) {
                numeroRandomicoBtnResult1 = (int) (Math.random() * 10);
                numeroRandomicoBtnResult2 = (int) (Math.random() * 10);
                numeroRandomicoBtnResult3 = (int) (Math.random() * 10);
                numeroRandomicoBtnResult4 = (int) (Math.random() * 10);
            } else if (RESULTADO_CORRETO <= 99) {
                numeroRandomicoBtnResult1 = (int) (Math.random() * 100);
                numeroRandomicoBtnResult2 = (int) (Math.random() * 100);
                numeroRandomicoBtnResult3 = (int) (Math.random() * 100);
                numeroRandomicoBtnResult4 = (int) (Math.random() * 100);
            } else {
                numeroRandomicoBtnResult1 = (int) (Math.random() * 1000);
                numeroRandomicoBtnResult2 = (int) (Math.random() * 1000);
                numeroRandomicoBtnResult3 = (int) (Math.random() * 1000);
                numeroRandomicoBtnResult4 = (int) (Math.random() * 1000);
            }

            if (numeroRandomicoBtnResult1 == numeroRandomicoBtnResult2) {
                numeroRandomicoBtnResult1++;
            } else if (numeroRandomicoBtnResult1 == numeroRandomicoBtnResult3) {
                numeroRandomicoBtnResult1++;
            } else if (numeroRandomicoBtnResult1 == numeroRandomicoBtnResult4) {
                numeroRandomicoBtnResult1++;
            } else if (numeroRandomicoBtnResult2 == numeroRandomicoBtnResult1) {
                numeroRandomicoBtnResult2++;
            } else if (numeroRandomicoBtnResult2 == numeroRandomicoBtnResult3) {
                numeroRandomicoBtnResult2++;
            } else if (numeroRandomicoBtnResult2 == numeroRandomicoBtnResult4) {
                numeroRandomicoBtnResult2++;
            } else if (numeroRandomicoBtnResult3 == numeroRandomicoBtnResult1) {
                numeroRandomicoBtnResult3++;
            } else if (numeroRandomicoBtnResult3 == numeroRandomicoBtnResult2) {
                numeroRandomicoBtnResult3++;
            } else if (numeroRandomicoBtnResult3 == numeroRandomicoBtnResult4) {
                numeroRandomicoBtnResult3++;
            } else if (numeroRandomicoBtnResult4 == numeroRandomicoBtnResult1) {
                numeroRandomicoBtnResult4++;
            } else if (numeroRandomicoBtnResult4 == numeroRandomicoBtnResult2) {
                numeroRandomicoBtnResult4++;
            } else if (numeroRandomicoBtnResult4 == numeroRandomicoBtnResult3) {
                numeroRandomicoBtnResult4++;
            }

            if (numeroRandomicoBtnResult1 == RESULTADO_CORRETO) {
                numeroRandomicoBtnResult1++;
            } else if (numeroRandomicoBtnResult2 == RESULTADO_CORRETO) {
                numeroRandomicoBtnResult2++;
            } else if (numeroRandomicoBtnResult3 == RESULTADO_CORRETO) {
                numeroRandomicoBtnResult3++;
            } else if (numeroRandomicoBtnResult4 == RESULTADO_CORRETO) {
                numeroRandomicoBtnResult4++;
            }

            if (numeroAleatorioParaAEscolhaDeQualBotaoASerPreenchido == 1) {
                btnResult1.setText(String.valueOf(NUMERO_RANDOMICO_2));
                btnResult2.setText(String.valueOf(numeroRandomicoBtnResult2));
                btnResult3.setText(String.valueOf(numeroRandomicoBtnResult3));
                btnResult4.setText(String.valueOf(numeroRandomicoBtnResult4));

                btnResult1.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        acertar();

                        return true;
                    }
                });

                btnResult2.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        errar();

                        return true;
                    }
                });

                btnResult3.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        errar();

                        return true;
                    }
                });

                btnResult4.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        errar();

                        return true;
                    }
                });

            } else if (numeroAleatorioParaAEscolhaDeQualBotaoASerPreenchido == 2) {
                btnResult1.setText(String.valueOf(numeroRandomicoBtnResult1));
                btnResult2.setText(String.valueOf(NUMERO_RANDOMICO_2));
                btnResult3.setText(String.valueOf(numeroRandomicoBtnResult3));
                btnResult4.setText(String.valueOf(numeroRandomicoBtnResult4));

                btnResult1.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        errar();

                        return true;
                    }
                });

                btnResult2.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        acertar();

                        return true;
                    }
                });

                btnResult3.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        errar();

                        return true;
                    }
                });

                btnResult4.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        errar();

                        return true;
                    }
                });

            } else if (numeroAleatorioParaAEscolhaDeQualBotaoASerPreenchido == 3) {
                btnResult1.setText(String.valueOf(numeroRandomicoBtnResult1));
                btnResult2.setText(String.valueOf(numeroRandomicoBtnResult2));
                btnResult3.setText(String.valueOf(NUMERO_RANDOMICO_2));
                btnResult4.setText(String.valueOf(numeroRandomicoBtnResult4));

                btnResult1.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        errar();

                        return true;
                    }
                });

                btnResult2.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        errar();

                        return true;
                    }
                });

                btnResult3.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        acertar();

                        return true;
                    }
                });

                btnResult4.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        errar();

                        return true;
                    }
                });

            } else if (numeroAleatorioParaAEscolhaDeQualBotaoASerPreenchido == 4) {
                btnResult1.setText(String.valueOf(numeroRandomicoBtnResult1));
                btnResult2.setText(String.valueOf(numeroRandomicoBtnResult2));
                btnResult3.setText(String.valueOf(numeroRandomicoBtnResult3));
                btnResult4.setText(String.valueOf(NUMERO_RANDOMICO_2));

                btnResult1.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        errar();

                        return true;
                    }
                });

                btnResult2.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        errar();

                        return true;
                    }
                });

                btnResult3.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        errar();

                        return true;
                    }
                });

                btnResult4.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        acertar();

                        return true;
                    }
                });

            }

            btnParar.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    irParaActivityPausa13();

                    return true;
                }
            });

        } else if (TXT_SINAL.getText().toString().equals(getString(R.string.porcentagem))) {
            NUMERO_RANDOM_FLOAT = random.nextFloat();

            RESULTADO_CORRETO = numeroRandomico1 * NUMERO_RANDOM_FLOAT;

            String RESULTADO_CORRETOFormatado = String.format(String.valueOf(DF.format(RESULTADO_CORRETO)).replace(".", ","));
            String NUMERO_RANDOM_FLOATFormatado = String.format(String.valueOf(DF.format(NUMERO_RANDOM_FLOAT)).replace(".", ","));

            TXT_NUM_1.setText(String.valueOf(numeroRandomico1));
            TXT_NUM_2.setText(String.valueOf(RESULTADO_CORRETOFormatado));

            TEXT_TO_SPEECH.speak("Qual valor em porcentagem multiplicado a " + String.valueOf(numeroRandomico1) +
                            " da o resultado de " + String.valueOf(RESULTADO_CORRETOFormatado),
                    TextToSpeech.QUEUE_FLUSH, null);

            float numeroRandomicoBtnResult1 = 0f;
            float numeroRandomicoBtnResult2 = 0f;
            float numeroRandomicoBtnResult3 = 0f;
            float numeroRandomicoBtnResult4 = 0f;

            if (RESULTADO_CORRETO <= 9) {
                numeroRandomicoBtnResult1 = random.nextFloat();
                numeroRandomicoBtnResult2 = random.nextFloat();
                numeroRandomicoBtnResult3 = random.nextFloat();
                numeroRandomicoBtnResult4 = random.nextFloat();
            } else if (RESULTADO_CORRETO <= 99) {
                numeroRandomicoBtnResult1 = random.nextFloat();
                numeroRandomicoBtnResult2 = random.nextFloat();
                numeroRandomicoBtnResult3 = random.nextFloat();
                numeroRandomicoBtnResult4 = random.nextFloat();
            } else {
                numeroRandomicoBtnResult1 = random.nextFloat();
                numeroRandomicoBtnResult2 = random.nextFloat();
                numeroRandomicoBtnResult3 = random.nextFloat();
                numeroRandomicoBtnResult4 = random.nextFloat();
            }

            String nRBR1 = String.format(String.valueOf(DF.format(numeroRandomicoBtnResult1)));
            String nRBR2 = String.format(String.valueOf(DF.format(numeroRandomicoBtnResult2)));
            String nRBR3 = String.format(String.valueOf(DF.format(numeroRandomicoBtnResult3)));
            String nRBR4 = String.format(String.valueOf(DF.format(numeroRandomicoBtnResult4)));

            if (numeroRandomicoBtnResult1 == numeroRandomicoBtnResult2) {
                numeroRandomicoBtnResult1++;
            } else if (numeroRandomicoBtnResult1 == numeroRandomicoBtnResult3) {
                numeroRandomicoBtnResult1++;
            } else if (numeroRandomicoBtnResult1 == numeroRandomicoBtnResult4) {
                numeroRandomicoBtnResult1++;
            } else if (numeroRandomicoBtnResult2 == numeroRandomicoBtnResult1) {
                numeroRandomicoBtnResult2++;
            } else if (numeroRandomicoBtnResult2 == numeroRandomicoBtnResult3) {
                numeroRandomicoBtnResult2++;
            } else if (numeroRandomicoBtnResult2 == numeroRandomicoBtnResult4) {
                numeroRandomicoBtnResult2++;
            } else if (numeroRandomicoBtnResult3 == numeroRandomicoBtnResult1) {
                numeroRandomicoBtnResult3++;
            } else if (numeroRandomicoBtnResult3 == numeroRandomicoBtnResult2) {
                numeroRandomicoBtnResult3++;
            } else if (numeroRandomicoBtnResult3 == numeroRandomicoBtnResult4) {
                numeroRandomicoBtnResult3++;
            } else if (numeroRandomicoBtnResult4 == numeroRandomicoBtnResult1) {
                numeroRandomicoBtnResult4++;
            } else if (numeroRandomicoBtnResult4 == numeroRandomicoBtnResult2) {
                numeroRandomicoBtnResult4++;
            } else if (numeroRandomicoBtnResult4 == numeroRandomicoBtnResult3) {
                numeroRandomicoBtnResult4++;
            }

            if (numeroRandomicoBtnResult1 == RESULTADO_CORRETO) {
                numeroRandomicoBtnResult1++;
            } else if (numeroRandomicoBtnResult2 == RESULTADO_CORRETO) {
                numeroRandomicoBtnResult2++;
            } else if (numeroRandomicoBtnResult3 == RESULTADO_CORRETO) {
                numeroRandomicoBtnResult3++;
            } else if (numeroRandomicoBtnResult4 == RESULTADO_CORRETO) {
                numeroRandomicoBtnResult4++;
            }

            if (numeroAleatorioParaAEscolhaDeQualBotaoASerPreenchido == 1) {
                btnResult1.setText(String.valueOf(NUMERO_RANDOM_FLOATFormatado));
                btnResult2.setText(String.valueOf(nRBR2));
                btnResult3.setText(String.valueOf(nRBR3));
                btnResult4.setText(String.valueOf(nRBR4));

                btnResult1.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        acertar();

                        return true;
                    }
                });

                btnResult2.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        errar();

                        return true;
                    }
                });

                btnResult3.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        errar();

                        return true;
                    }
                });

                btnResult4.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        errar();

                        return true;
                    }
                });

            } else if (numeroAleatorioParaAEscolhaDeQualBotaoASerPreenchido == 2) {
                btnResult1.setText(String.valueOf(nRBR1));
                btnResult2.setText(String.valueOf(NUMERO_RANDOM_FLOATFormatado));
                btnResult3.setText(String.valueOf(nRBR3));
                btnResult4.setText(String.valueOf(nRBR4));

                btnResult1.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        errar();

                        return true;
                    }
                });

                btnResult2.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        acertar();

                        return true;
                    }
                });

                btnResult3.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        errar();

                        return true;
                    }
                });

                btnResult4.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        errar();

                        return true;
                    }
                });

            } else if (numeroAleatorioParaAEscolhaDeQualBotaoASerPreenchido == 3) {
                btnResult1.setText(String.valueOf(nRBR1));
                btnResult2.setText(String.valueOf(nRBR2));
                btnResult3.setText(String.valueOf(NUMERO_RANDOM_FLOATFormatado));
                btnResult4.setText(String.valueOf(nRBR4));

                btnResult1.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        errar();

                        return true;
                    }
                });

                btnResult2.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        errar();

                        return true;
                    }
                });

                btnResult3.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        acertar();

                        return true;
                    }
                });

                btnResult4.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        errar();

                        return true;
                    }
                });

            } else if (numeroAleatorioParaAEscolhaDeQualBotaoASerPreenchido == 4) {
                btnResult1.setText(String.valueOf(nRBR1));
                btnResult2.setText(String.valueOf(nRBR2));
                btnResult3.setText(String.valueOf(nRBR3));
                btnResult4.setText(String.valueOf(NUMERO_RANDOM_FLOATFormatado));

                btnResult1.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        errar();

                        return true;
                    }
                });

                btnResult2.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        errar();

                        return true;
                    }
                });

                btnResult3.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        errar();

                        return true;
                    }
                });

                btnResult4.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        acertar();

                        return true;
                    }
                });

            }

            btnParar.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    irParaActivityPausa13();

                    return true;
                }
            });
        }
    }

    public void iniciarTempo() {
        TIMER = new Timer();
        TIMER.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (seconds == 5) {
                            TEXT_TO_SPEECH.speak("Faltam apenas 5 segundos", TextToSpeech.QUEUE_FLUSH, null);
                        }

                        if (seconds == 0) {
                            TIMER.cancel();
                            bloquearBotoesResultados();

                            if ((TXT_SINAL.getText().toString().equals(getString(R.string.mais))) || (TXT_SINAL.getText().toString().equals(getString(R.string.menos)))) {
                                TEXT_TO_SPEECH.speak("Seu tempo acabou vamos para o próximo desafio, o resultado era de " + String.valueOf(NUMERO_RANDOMICO_2),
                                        TextToSpeech.QUEUE_FLUSH, null);
                            } else if (TXT_SINAL.getText().toString().equals(getString(R.string.porcentagem))) {
                                TEXT_TO_SPEECH.speak("Seu tempo acabou vamos para o próximo desafio, o resultado era de" +
                                                String.valueOf(String.format(String.valueOf(DF.format(NUMERO_RANDOM_FLOAT)).replace(".", ","))),
                                        TextToSpeech.QUEUE_FLUSH, null);
                            }

                            boolean speakingEnd = TEXT_TO_SPEECH.isSpeaking();

                            do {
                                speakingEnd = TEXT_TO_SPEECH.isSpeaking();
                            } while (speakingEnd);

                            seconds = 30;
                            iniciarTempo();
                            carregarEstrutura();
                        }

                        TXT_TEMPO.setText(String.valueOf(seconds));
                        seconds -= 1;
                    }
                });
            }
        }, 0, 1000);
    }

    public void recuperandoReferencia() {
        TXT_NUM_1       = findViewById(R.id.txtNum1);
        TXT_NUM_2       = findViewById(R.id.txtNum2);
        TXT_TEMPO       = findViewById(R.id.txtTempo);
        TXT_PONTUACAO   = findViewById(R.id.txtPontuacao);
        TXT_SINAL       = findViewById(R.id.txtSinal);
        btnResult1      = findViewById(R.id.btnResult1);
        btnResult2      = findViewById(R.id.btnResult2);
        btnResult3      = findViewById(R.id.btnResult3);
        btnResult4      = findViewById(R.id.btnResult4);
        btnParar        = findViewById(R.id.btnParar);
        btnRepetir      = findViewById(R.id.btnRepetir);
    }

    @Override
    public void onInit(int text) {
        if (text == TextToSpeech.SUCCESS) {
            int language = TEXT_TO_SPEECH.setLanguage(Locale.getDefault());

            if (language == TextToSpeech.LANG_MISSING_DATA || language == TextToSpeech.LANG_NOT_SUPPORTED) {
                speakOutNow();
            }
        } else {
            MessageBox.show(Act13.this, "Erro", "Erro no TextSpeech!");
        }
    }

    private void speakOutNow() {
        if (btnResult1.isPressed()) {
            String speech = btnResult1.getText().toString();
            TEXT_TO_SPEECH.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
        }

        if (btnResult2.isPressed()) {
            String speech = btnResult2.getText().toString();
            TEXT_TO_SPEECH.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
        }

        if (btnResult3.isPressed()) {
            String speech = btnResult3.getText().toString();
            TEXT_TO_SPEECH.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
        }

        if (btnResult4.isPressed()) {
            String speech = btnResult4.getText().toString();
            TEXT_TO_SPEECH.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
        }

        if (btnParar.isPressed()) {
            TEXT_TO_SPEECH.speak("Pausar o jogo", TextToSpeech.QUEUE_FLUSH, null);
        }

        if (btnRepetir.isPressed()) {
            if (TXT_SINAL.getText().toString().equals("+")) {
                TEXT_TO_SPEECH.speak("Repetindo a expressão, qual valor somado a " + TXT_NUM_1.getText().toString() +
                                " da o resultado de " + String.valueOf(RESULTADO_CORRETO),
                        TextToSpeech.QUEUE_FLUSH, null);
            } else if (TXT_SINAL.getText().toString().equals("-")) {
                TEXT_TO_SPEECH.speak("Repetindo a expressão, qual valor subtraído a " + TXT_NUM_1.getText().toString() +
                                " da o resultado de " + String.valueOf(RESULTADO_CORRETO),
                        TextToSpeech.QUEUE_FLUSH, null);
            } else if (TXT_SINAL.getText().toString().equals("%")) {
                TEXT_TO_SPEECH.speak("Repetindo a expressão, qual valor em porcentagem multiplicado a " + TXT_NUM_1.getText().toString() +
                                " da o resultado de " + String.valueOf(RESULTADO_CORRETO),
                        TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    @Override
    public void onClick(View view) {
    }

    public void ouvintes() {
        btnResult1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOutNow();
            }
        });

        btnResult2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOutNow();
            }
        });

        btnResult3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOutNow();
            }
        });

        btnResult4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOutNow();
            }
        });

        btnParar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOutNow();
            }
        });

        btnRepetir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOutNow();
            }
        });
    }

    private void acertarSpeak() {
        int numeroRandom = random.nextInt(5) + 1;

        if (numeroRandom == 1) {
            TEXT_TO_SPEECH.speak("Parabéns você acertou", TextToSpeech.QUEUE_FLUSH, null);
        } else if (numeroRandom == 2) {
            TEXT_TO_SPEECH.speak("Excelente acertou", TextToSpeech.QUEUE_FLUSH, null);
        } else if (numeroRandom == 3) {
            TEXT_TO_SPEECH.speak("Parabéns acertou você está ficando bom", TextToSpeech.QUEUE_FLUSH, null);
        } else if (numeroRandom == 4) {
            TEXT_TO_SPEECH.speak("Uau!, você acertou", TextToSpeech.QUEUE_FLUSH, null);
        } else if (numeroRandom == 5) {
            TEXT_TO_SPEECH.speak("Acertou de novo", TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void errarSpeak() {
        int numeroRandom = random.nextInt(5) + 1;

        if (numeroRandom == 1) {
            if ((TXT_SINAL.getText().toString().equals(getString(R.string.mais))) || (TXT_SINAL.getText().toString().equals(getString(R.string.menos)))) {
                TEXT_TO_SPEECH.speak("Infelizmente você errou, o resultado era de " + String.valueOf(NUMERO_RANDOMICO_2),
                        TextToSpeech.QUEUE_FLUSH, null);
            } else if (TXT_SINAL.getText().toString().equals(getString(R.string.porcentagem))) {
                TEXT_TO_SPEECH.speak("Infelizmente você errou, o resultado era de " +
                                String.valueOf(String.format(String.valueOf(DF.format(NUMERO_RANDOM_FLOAT)).replace(".", ","))),
                        TextToSpeech.QUEUE_FLUSH, null);
            }
        } else if (numeroRandom == 2) {
            if ((TXT_SINAL.getText().toString().equals(getString(R.string.mais))) || (TXT_SINAL.getText().toString().equals(getString(R.string.menos)))) {
                TEXT_TO_SPEECH.speak("Não foi dessa vez você errou, o resultado era de " + String.valueOf(NUMERO_RANDOMICO_2),
                        TextToSpeech.QUEUE_FLUSH, null);
            } else if (TXT_SINAL.getText().toString().equals(getString(R.string.porcentagem))) {
                TEXT_TO_SPEECH.speak("Não foi dessa vez você errou, o resultado era de " +
                                String.valueOf(String.format(String.valueOf(DF.format(NUMERO_RANDOM_FLOAT)).replace(".", ","))),
                        TextToSpeech.QUEUE_FLUSH, null);
            }
        } else if (numeroRandom == 3) {
            if ((TXT_SINAL.getText().toString().equals(getString(R.string.mais))) || (TXT_SINAL.getText().toString().equals(getString(R.string.menos)))) {
                TEXT_TO_SPEECH.speak("Tente mais uma vez, o resultado era de " + String.valueOf(NUMERO_RANDOMICO_2),
                        TextToSpeech.QUEUE_FLUSH, null);
            } else if (TXT_SINAL.getText().toString().equals(getString(R.string.porcentagem))) {
                TEXT_TO_SPEECH.speak("Tente mais uma vez, o resultado era de " +
                                String.valueOf(String.format(String.valueOf(DF.format(NUMERO_RANDOM_FLOAT)).replace(".", ","))),
                        TextToSpeech.QUEUE_FLUSH, null);
            }
        } else if (numeroRandom == 4) {
            if ((TXT_SINAL.getText().toString().equals(getString(R.string.mais))) || (TXT_SINAL.getText().toString().equals(getString(R.string.menos)))) {
                TEXT_TO_SPEECH.speak("Quem sabe da próxima vez, o resultado era de " + String.valueOf(NUMERO_RANDOMICO_2),
                        TextToSpeech.QUEUE_FLUSH, null);
            } else if (TXT_SINAL.getText().toString().equals(getString(R.string.porcentagem))) {
                TEXT_TO_SPEECH.speak("Quem sabe da próxima vez, o resultado era de " +
                                String.valueOf(String.format(String.valueOf(DF.format(NUMERO_RANDOM_FLOAT)).replace(".", ","))),
                        TextToSpeech.QUEUE_FLUSH, null);
            }
        } else if (numeroRandom == 5) {
            if ((TXT_SINAL.getText().toString().equals(getString(R.string.mais))) || (TXT_SINAL.getText().toString().equals(getString(R.string.menos)))) {
                TEXT_TO_SPEECH.speak("Que pena tente de novo, o resultado era de " + String.valueOf(NUMERO_RANDOMICO_2),
                        TextToSpeech.QUEUE_FLUSH, null);
            } else if (TXT_SINAL.getText().toString().equals(getString(R.string.porcentagem))) {
                TEXT_TO_SPEECH.speak("Que pena tente de novo, o resultado era de " +
                                String.valueOf(String.format(String.valueOf(DF.format(NUMERO_RANDOM_FLOAT)).replace(".", ","))),
                        TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    private void acertar() {
        TIMER.cancel();
        acertarSpeak();

        boolean speakingEnd = TEXT_TO_SPEECH.isSpeaking();

        do {
            speakingEnd = TEXT_TO_SPEECH.isSpeaking();
        } while (speakingEnd);

        seconds = 30;
        iniciarTempo();
        carregarEstrutura();
        numeroAleatorioParaAEscolhaDeQualBotaoASerPreenchido = random.nextInt(4) + 1;
        TXT_PONTUACAO.setText(String.valueOf(Integer.valueOf((String) TXT_PONTUACAO.getText()) + 1));
    }

    private void errar() {
        TIMER.cancel();
        errarSpeak();

        boolean speakingEnd = TEXT_TO_SPEECH.isSpeaking();

        do {
            speakingEnd = TEXT_TO_SPEECH.isSpeaking();
        } while (speakingEnd);

        seconds = 30;
        iniciarTempo();
        carregarEstrutura();
        numeroAleatorioParaAEscolhaDeQualBotaoASerPreenchido = random.nextInt(4) + 1;
    }

    private void desbloquearBotoesResultados() {
        btnResult1.setEnabled(true);
        btnResult2.setEnabled(true);
        btnResult3.setEnabled(true);
        btnResult4.setEnabled(true);
        btnParar.setEnabled(true);
        btnRepetir.setEnabled(true);
    }

    private void bloquearBotoesResultados() {
        btnResult1.setEnabled(false);
        btnResult2.setEnabled(false);
        btnResult3.setEnabled(false);
        btnResult4.setEnabled(false);
        btnParar.setEnabled(false);
        btnRepetir.setEnabled(false);
    }

    private void irParaActivityPausa13() {
        Integer tempo = Integer.parseInt(String.valueOf(TXT_TEMPO.getText()));
        String pontos = TXT_PONTUACAO.getText().toString();
        TIMER.cancel();
        Intent it = new Intent(Act13.this, ActPausa13.class);
        it.putExtra("TXT_TEMPO", tempo);
        it.putExtra("TXT_PONTUACAO", pontos);
        startActivity(it);
    }

}