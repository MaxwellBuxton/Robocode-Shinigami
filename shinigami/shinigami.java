import dev.robocode.tankroyale.botapi.*;
import dev.robocode.tankroyale.botapi.events.*;

public class shinigami extends Bot{
    public static void main(String[] args){
        new shinigami().start();
    }

    shinigami(){
        super(BotInfo.fromFile("shinigami.json"));
    }

    public void run(){
        setAdjustGunForBodyTurn(true);
        setAdjustRadarForBodyTurn(true);
        setAdjustRadarForGunTurn(true);
        setBodyColor(Color.PURPLE);
        setScanColor(Color.RED);
        setBulletColor(Color.PURPLE);
        while(isRunning()){
            turnRadarRight(20);
            //foresight();
        }
    }

    public double grader(double[] toNode,double stepy){
        double[] node = toNode.clone();
        double score = 0;
        double width = getArenaWidth();
        double height = getArenaHeight();
        score = score + (500*(Math.pow(1.01,-1*Math.abs(0-node[0]))))+500*(Math.pow(1.01,-1*Math.abs(width-node[0])))+(500*(Math.pow(1.01,-1*Math.abs(0-node[1]))))+500*(Math.pow(1.01,-1*Math.abs(height-node[1])));
        if(node[4] == 1){
            score = score + 20;
        }
        score = score * stepy;
        //System.out.println(node[0]+""+node[1]+""+score);
        return score*-1;
    }

    public double[][] bubbleSort(double[][] toSort){
        // i am pretty sure that if i wrote this in a interview i would not get the job lol
        double[][] nodes = toSort.clone();
        double[] temp = new double[6];
        for(int i = 0; i<4;i++){
            for(int n = 0; n<17;n++){
                if(nodes[n][5] > nodes[n+1][5]){
                    temp = nodes[n+1];
                    nodes[n+1] = nodes[n];
                    nodes[n] = temp;
                }
            }
        }
        double[][] result = {nodes[17],nodes[16]};
        return result;
    }

    public void foresight(){
        double dTree[][][] = new double[11][2500][6];
        double nodeStor[][] = new double[18][6];
        double speed = getSpeed();
        double x = getX();
        double y = getY();
        double dir = getDirection();
        //System.out.println("initializing complete");
        // initial time step
        int dang = -4;
        for(int i=0; i<9; i++){
            //x,y,direction,parentnode,reverse,score
            nodeStor[i][0] = x+Math.cos(Math.toRadians(dir + dang)*8);
            nodeStor[i][1] = y+Math.sin(Math.toRadians(dir + dang)*8);
            nodeStor[i][2] = dir + dang;
            nodeStor[i][3] = 0;
            nodeStor[i][4] = 0;
            nodeStor[i][5] = grader(nodeStor[i],1);
            dang++;
        }
        dang = -4;
        for(int i=0; i<9;i++){
            nodeStor[i+9][0] = x+Math.cos(Math.toRadians(dir + dang)*(-1*(8)));
            nodeStor[i+9][1] = y+Math.sin(Math.toRadians(dir + dang)*(-1*(8)));
            nodeStor[i+9][2] = dir + dang;
            nodeStor[i+9][3] = 0;
            nodeStor[i+9][4] = 1;
            nodeStor[i+9][5] = grader(nodeStor[9+i],1);
            dang++;
        }
        double[][] number15 = bubbleSort(nodeStor);
        dTree[0][0] = number15[0].clone();
        dTree[0][1] = number15[1].clone();
        //System.out.println("first stage complete");
        for(int i=0;i<10;i++){
            for(int n=0;n<Math.pow(2,i+1);n++){
                dang = -4;
                for(int m=0; m<9; m++){
                    //x,y,direction,parentnode,reverse,score
                    nodeStor[m][0] = dTree[i][n][0]+Math.cos(Math.toRadians(dTree[i][n][2]+dang)*8);
                    nodeStor[m][1] = dTree[i][n][1]+Math.sin(Math.toRadians(dTree[i][n][2]+dang)*8);
                    nodeStor[m][2] = dTree[i][n][2] + dang;
                    nodeStor[m][3] = n;
                    nodeStor[m][4] = 0;
                    nodeStor[m][5] = grader(nodeStor[m],(i+2));
                    dang++;
                }
                if(i==5){
                    //System.out.println(dTree[3][4][3]+"first");
                }

                dang = -4;
                for(int m=0; m<9; m++){
                    //x,y,direction,parentnode,reverse,score
                    nodeStor[m+9][0] = dTree[i][n][0]+Math.cos(Math.toRadians(dTree[i][n][2]+dang)*(-1*8));
                    nodeStor[m+9][1] = dTree[i][n][1]+Math.sin(Math.toRadians(dTree[i][n][2]+dang)*(-1*8));
                    nodeStor[m+9][2] = dTree[i][n][2] + dang;
                    nodeStor[m+9][3] = n;
                    nodeStor[m+9][4] = 1;
                    nodeStor[m+9][5] = grader(nodeStor[m+9],(i+2));
                    dang++;
                }
                if(i==0){
                    System.out.println(nodeStor[0][5]+nodeStor[8][5]);
                }
                number15 = bubbleSort(nodeStor);
                dTree[i+1][((n+1)*2)-2] = number15[0].clone();
                dTree[i+1][((n+1)*2)-1] = number15[1].clone();
                if(i==5){
                    //System.out.println(dTree[3][4][3]+"last");
                }

            }
        }
        //System.out.println("stage 2 complete");
        // data creation complete now find optimal path

        double hScore = -1000000;
        int fPointer = 0;
        for(int i=0;i<Math.pow(2,11);i++){
            int parent = i;
            double tScore = 0;
            //System.out.println(dTree[3][4][3]);
            for(int n=0;n<11;n++){
               tScore = dTree[10-n][parent][5] + tScore;
               parent = (int)dTree[10-n][parent][3];
            }
            dTree[10][i][5] = tScore;
            //System.out.println(tScore);
            if(tScore > hScore){
                hScore = tScore;
                fPointer = i;
                //System.out.print(tScore+" "+hScore+" "+parent);
            }
        }
        // ding! ding! ding! we have a winner!!
        int parent = fPointer;
        for(int i=0;i<10;i++){
            parent = (int)dTree[10-i][parent][3];
            //System.out.println(parent);
        }
        double[] winner = dTree[0][parent].clone();
        System.out.println(fPointer+" "+(winner[2]-getDirection())+" "+hScore);
        if(winner[4] == 1){
            setForward(-8);
        }
        else{
            setForward(8);
        }
        setTurnRight(winner[2]-getDirection());
        //System.out.println(hScore+" "+parent+" "+winner[5]);
    }

    public double[] prediaim(ScannedBotEvent e){
        double timeDif = 40;
        double deltaX = 0;
        double deltaY = 0;
        for(int x = 0; x < 40 && Math.abs(timeDif) > 1; x++){
            deltaX = Math.cos(Math.toRadians(e.getDirection())) * (e.getSpeed()*x);
            deltaY = Math.sin(Math.toRadians(e.getDirection())) * (e.getSpeed()*x);
            double opp = (e.getY() + deltaY) - getY();
            double ajd = (e.getX() + deltaX) - getX();
            double angR = Math.atan(opp/ajd);
            double hyp = Math.abs(opp / Math.sin(angR));
            timeDif = x - (hyp / 17);
            //System.out.println(x + " " + Math.round(timeDif) + " " + Math.round(deltaX) + " " + Math.round(deltaY) + " " + Math.round(hyp));
        }
        if(timeDif < 5){
            double result[] = {deltaX,deltaY};
            return result;
        }
        else{
            double result[] = {0,0};
            return result;
        }
    }

    public void onScannedBot(ScannedBotEvent e){
        foresight();
        double radber = calcRadarBearing(directionTo(e.getX(),e.getY()));
        double aim[] = prediaim(e);
        double deltaX = aim[0];
        double deltaY = aim[1];
        double gunber = calcGunBearing(directionTo(e.getX() + deltaX,e.getY() + deltaY));
        
        setTurnRadarLeft(radber);
        setTurnGunLeft(gunber);
        
        if (gunber < 3){
            fire(1);
        }


    }
}