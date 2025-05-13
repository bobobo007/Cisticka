import React from "react";
import { Card, CardContent } from "../ui/card";
import { Button } from "../ui/button";
import { Progress } from "../ui/progress";
import { WifiIcon, Thermometer, Droplet, ChevronUp, ChevronDown } from "lucide-react";
import { useState } from "react";

export default function CistickaUI() {
  const [isValve1Open, setIsValve1Open] = useState(false);
  const [isValve2Open, setIsValve2Open] = useState(false);
  const [isHeating, setIsHeating] = useState(false);
  const waterLevel = 75; // Example value, in percent
  const temperature = 22.5; // Example value, in Celsius
  const wifiSignal = -65; // Example value, in dB

  return (
    <div className="p-4 grid grid-cols-1 gap-4 md:grid-cols-2">
      {/* Water Level Card */}
      <Card className="bg-blue-100">
        <CardContent>
          <h2 className="text-xl font-semibold text-blue-600 flex items-center">
            <Droplet className="mr-2" /> Hladina vody
          </h2>
          <div className="mt-4">
            <Progress value={waterLevel} className="h-6" />
            <p className="mt-2 text-center text-lg">{waterLevel}%</p>
          </div>
        </CardContent>
      </Card>

      {/* Temperature and Heating Card */}
      <Card className="bg-red-100">
        <CardContent>
          <h2 className="text-xl font-semibold text-red-600 flex items-center">
            <Thermometer className="mr-2" /> Teplota na ventile
          </h2>
          <p className="mt-4 text-center text-2xl font-bold">{temperature}°C</p>
          <Button
            className={`mt-4 w-full ${isHeating ? "bg-red-600" : "bg-red-400"}`}
            onClick={() => setIsHeating(!isHeating)}
          >
            {isHeating ? "Vypnúť nahrievanie" : "Zapnúť nahrievanie"}
          </Button>
        </CardContent>
      </Card>

      {/* Valve Control Card */}
      <Card className="bg-green-100 col-span-2">
        <CardContent>
          <h2 className="text-xl font-semibold text-green-600 flex items-center">
            <ChevronUp className="mr-2" /> Ovládanie ventilov
          </h2>
          <div className="mt-4 grid grid-cols-2 gap-4">
            <Button
              className={`w-full ${isValve1Open ? "bg-green-600" : "bg-green-400"}`}
              onClick={() => setIsValve1Open(!isValve1Open)}
            >
              {isValve1Open ? "Zavrieť odkaľovací ventil" : "Otvoriť odkaľovací ventil"}
            </Button>
            <Button
              className={`w-full ${isValve2Open ? "bg-green-600" : "bg-green-400"}`}
              onClick={() => setIsValve2Open(!isValve2Open)}
            >
              {isValve2Open ? "Zavrieť čistiaci ventil" : "Otvoriť čistiaci ventil"}
            </Button>
          </div>
        </CardContent>
      </Card>

      {/* WiFi Signal Card */}
      <Card className="bg-yellow-100">
        <CardContent>
          <h2 className="text-xl font-semibold text-yellow-600 flex items-center">
            <WifiIcon className="mr-2" /> WiFi signál
          </h2>
          <p className="mt-4 text-center text-2xl font-bold">{wifiSignal} dB</p>
        </CardContent>
      </Card>
    </div>
  );
}
